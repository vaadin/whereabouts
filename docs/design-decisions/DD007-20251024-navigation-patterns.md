# Design Decision 007: Navigation Patterns

* Recorded: 2025-10-24
* Recorded by: <petter@vaadin.com>

Whereabouts aims to store relevant UI state in URL parameters, either as route parameters or query parameters. At the
time of writing, this only applies to grid selections, but more cases will be added in the future.

URL parameters are basically strings and numbers. In code, however, I want to use richer types like value objects.
Furthermore, as more parameters are added to a view, the code to navigate to the view with all the parameters becomes
more complex.

I therefore decided to create specific navigation methods for every navigation case:

```java
public final class ProjectsNavigation {

    private ProjectsNavigation() {
    }

    public static void navigateToProjectList() {
        UI.getCurrent().navigate(ProjectListView.class);
    }

    public static void navigateToProjectDetails(ProjectId id) {
        UI.getCurrent().navigate(ProjectDetailsView.class,
                new RouteParam(ProjectDetailsView.PARAM_PROJECT_ID, id.toLong()));
    }
}
```

Now, if I want to open the project details from any other view, I can call
`ProjectsNavigation.navigateToProjectDetails(myProjectId)`. As the project details view gets more URL parameters, I
only need to refactor this method.

In my first version, I declared the static navigation methods inside the view class itself. That required the view to be
public, and I wanted to keep it package-protected to prevent accidental reuse. I then ended up with a separate
public `*Navigation` utility class in every package that contained views. You can think of it as the public API of the
user interface.
