# Design Decision 008: Master-Detail Views

* Recorded: 2025-10-24
* Recorded by: <petter@vaadin.com>

A common UI pattern in business applications is *master-detail*. The master view contains a list of items, and when you
select one, it shows up in the detail view. These two views are often visible at the same time, with the detail showing
up next to, or below, the master.

It is also best practice to update the URL so that the ID of the selected item is a part of it. That way, you can share
a link to the detail with your colleagues, bookmark it, etc. Thus, the URL of the master could be
`http://localhost:8080/products` and the URL of a particular detail `http://localhost:8080/products/[product-ID]`.

From the point of view of a Vaadin UI, implementing this pattern requires the following:

1. Fetch, filter, and sort the list of items in the master view.
2. Navigate to the corresponding detail view, using the master view as a router layout so that the details are rendered
   *inside* the master view.
3. Fetch the detailed data in the detail view and show it.
4. Update the master view after updating or deleting an item.

Unfortunately, this is not enough. You also need to keep the following in mind:

* If a user navigates directly to the detail view using a URL, the corresponding item should be selected in the master
  view.
* If a user navigates to the detail view using a URL, and the item does not exist, they should be redirected to the
  master view.
* If a user is editing an item in the detail view, and then selects another item in the master, what should happen to
  the unsaved changes?

And then of course the entire thing should be accessible and progressive.

## The MasterDetailLayout

Vaadin has a new `MasterDetailLayout` (currently behind a feature flag) that is designed to help with this particular
UI pattern. By having your view extend this layout, and adding some special annotations, it can act both as a standalone
view, and as a router layout for the detail view. This takes care of some of the UI problems out of the box. Whereabouts
uses this layout in all its master-detail views.

## Selection Management

Since the master and the detail are implemented as separate views and mapped to separate routes, you *select an item by
navigating to the detail view* and *clear the selection by navigating away from it (back to the masters view)*.

To implement this, you need to do two things:

1. Whenever you select an item (e.g. in a grid), you need to perform a call to `UI.navigate(..)`, passing in the ID of
   the selected item as a path parameter. In practice, this happens through the navigation utility methods described
   in [DD007](DD007-20251024-navigation-patterns.md).
2. The master view needs to implement `AfterNavigationObserver`. Inside `afterNavigation(..)`, you need to look up the
   item corresponding to the ID in the path, and select it in the UI. If the item does not exist, or if there is no ID
   in the path, you need to clear the UI selection.

What this does is effectively syncing the UI selection and a URL path parameter, with the URL path parameter acting as
the *source of truth*.

This leads to a problem with Vaadin, though: it is currently *not possible to select an item by its ID* in either Grid
or any of the other selection components. This means that you first have to find the item itself by its ID in order to
select it. If you are using pagination - which you should in large data sets - this involves an extra round trip to the
database. **I don't know why you can't select an item by its ID, but I hope we can fix this in a future Vaadin
version.**

## Fetching Details Data

For complex entities, you often don't fetch the complete entity in the master view. Rather, you perform a custom query
that returns DTOs with only the data that the master view needs. Then, inside the detail view, you fetch the complete
entity.

This means that the detail view also needs to implement `AfterNavigationObserver`. Inside `afterNavigation(..)`, it has
to extract the ID from the path parameter, fetch the corresponding entity from the database, and render it in the UI.
If the entity does not exist, it needs to navigate away from itself, back to the master view.

I first considered implementing `HasUrlParameter`, but I quickly ended up with route templates instead:

```java

@Route(value = "employees/:employeeId", layout = EmployeeListView.class)
class EmployeeDetailsView extends VerticalLayout implements AfterNavigationObserver {

    public static final String PARAM_EMPLOYEE_ID = "employeeId";
    // The rest of the view
}
```

The reason for this is that the route parameter needs to be accessed both from the detail view (to load the data) and
the master view (to manage the selection). Since the route parameter is declared in the detail view (in the `@Route`
annotation), I decided to put the parameter name constant in the detail view as well. The master view also uses this
constant when fetching the ID.

## Navigating Away While Editing

One way to solve this problem is to avoid editing inside the detail view completely. If you have an edit button, have
it open a dialog where the editing happens instead. Whereabouts does this in a few places.

However, if you need to edit inside the detail view, you can do that, too. In that case, the detail view needs to
implement `BeforeLeaveObserver`. In the `beforeLeave(..)` method, it should check whether the view is in edit mode and
decide what to do. It could, for instance, postpone the navigation event and open a `ConfirmDialog` that asks the user
what to do. Whereabouts also does this in a few places.

## Updating the Master After Editing

After an item has been edited in the detail view, it needs to inform the master so that it can refresh itself. Since
the detail view is rendered inside the master view, they are both part of the same component tree. In fact, while the
detail view is attached to the UI, *its parent component is the master view*. We can utilize that here.

I first tried creating a custom component event and firing it up the component tree using the `ComponentUtil.fireEvent`.
I soon realized this is overkill for this particular case: the master and detail view are already tightly coupled and
adding an event is not going to change that.

Instead, I ended up defining a method in the master view that looks something like this:

```java
// Note the package visibility - this method is only supposed to be called by the detail view.
void onItemUpdated(MyItemId itemId) {
    // Refresh the grid, maintain selection, etc.
}
```

Then, inside the detail view, I created a method for getting the master view like this:

```java
private Optional<MyMasterView> getMasterView() {
    return getParent().filter(MyMasterView.class::isInstance).map(MyMasterView.class::cast);
}
```

When the item is updated in the detail view, I can now inform the master like this:

```java
private void notifyMasterOfUpdatedItem(MyItemId updatedItemId) {
    getMasterView().ifPresent(masterView -> masterView.onItemUpdated(updatedItemId));
}
```

This works great, but revealed another problem with Vaadin. If the items are Java records - which they are in
Whereabouts - then their equality is based on their value. Since Grid uses object equality to identify which item is
selected, you will lose the selection after refreshing the grid.

There are a few ways you can solve this:

1. You can override `equals()` and `hashCode()` in your records to only compare the ID. This might have a negative
   effect elsewhere, for example if you write a test and need to assert that two records contain the same data.
2. You can configure the data provider to use the ID rather than the item itself when comparing items. **This is an
   option that I need to explore further, because I'm not entirely clear on how it works and what implications it has.**
3. You can store the ID of the selection in a temporary variable, refresh the grid, lookup the corresponding item and
   re-set the selection.

**At the time of writing, I have not yet decided which option is the best one. Option number 2 seems to be pretty close
to what I want, but I need to understand it better.**
