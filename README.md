# Whereabouts README

![The Employees View](/docs/images/employees.png)

_Whereabouts_ is an example application that demonstrates how to use Vaadin to build an enterprise application with
the following tech stack:

* Vaadin 25
* Spring Boot
* Java 21
* jOOQ
* PostgreSQL

More specifically, Whereabouts has been developed with the following aspects in mind:

* Using modern Java features, such as records and pattern matching.
* Using new Vaadin features, such as signals, `MasterDetailLayout`, and the new Aura theme.
* Keeping the package structure as simple as possible, inspired
  by [Spring Modulith](https://spring.io/projects/spring-modulith).
* Handling typical business application use cases, including:
    * Data pagination
    * Filtering and sorting
    * Forms and validation
    * Dialogs
    * Custom icons

Do you have an example use case you would like to see implemented in Whereabouts? Please create
an [issue](https://github.com/vaadin/whereabouts/issues)!

Or have you already implemented an example use case? Please create
a [pull request](https://github.com/vaadin/whereabouts/pulls)!

## A Work in Progress

![The Projects View](/docs/images/projects.png)

Whereabouts is not a finished application. Some features are missing, others only partially implemented. Some features
are polished, others have rough edges. We want Whereabouts to become a collection of Vaadin best practices within this
particular tech stack and fictional business use cases. As we gain new insights, you'll see the application evolve.

We invite you to participate in this process by challenging (or agreeing with) the design decisions in the application,
and creating [issues](https://github.com/vaadin/whereabouts/issues)
and [pull requests](https://github.com/vaadin/whereabouts/pulls)!

## Dogfooding

![The Locations View](/docs/images/locations.png)

Whereabouts is also about "eating our own dogfood". When exploring the source code, you'll notice some things are more
difficult to implement than you'd expect, or that you have to manually build some things that you'd expect would be
provided by the framework. You'll also find different ways of solving the same problem. Over time, as Vaadin evolves,
you'll hopefully see the code improve.

### Candidates for Improvement

* Path parameters are used as the source of truth for grid selections. However, even more UI state could be moved to
  the URL. Sorting and filtering could be stored in query parameters. The selected tab within a view could be stored as
  a path parameter. However, we would have to come up with a smooth way of keeping this information in sync.
* When working with jOOQ, it is natural to use IDs to refer to other entities. This is problematic when working with
  e.g. Grids and ComboBoxes that contain the entities themselves. To select an item in a grid, you first have to fetch
  the complete entity from the application service and then select it. To bind a combo box to a property that takes an
  ID, you need to create a converter that maps between the entity and the ID (simple), and the ID and the entity (needs
  a database call).
* Java records are quite useful as DTOs. However, using them with Vaadin's Binder is problematic . First, you have to
  bind them by name, losing type safety in the process. Second, you have to bind every record component, including
  hidden fields.
    * The fact that records are immutable makes many things easier, but when you need to change only one component,
      you have to re-create the entire record. You can create `with`-methods that makes the API nicer, but require some
      boilerplate coding. [JEP-468](https://bugs.openjdk.org/browse/JDK-8321133) could make this easier in the future.
* Signals will change how you think about user interface state management. However, because they are still being
  introduced, you'll see a mix of signals, binders, and data providers in Whereabouts. As signals are integrated into
  Vaadin, the structure and APIs will improve.
* Sorting and filtering grids while using pagination and jOOQ could use some improvement. How should you set a filter or
  a sort property in the UI and how should you translate them into a `Condition` or `OrderField` in jOOQ?
  In Whereabouts, you'll find different experiments, such as:
    * Declaring sortable properties as enums, to make them explicit and allow for pattern matching.
    * Using both strings and records as filter inputs.
    * Sorting by clicking on grid headers and selecting from a `Select` box.
    * Using Spring Data's `Paginable` to pass pagination and sorting information to the jOOQ query.
    * Using Vaadin's data structures to pass pagination and sorting information to the jOOQ query.
* Aura does not yet have theme variants or utility classes. Because of this, Whereabouts has quite a lot of direct
  manipulation of individual component styles.
* Security. Whereabouts uses its own user database, but provides no means of managing users,
  changing passwords, granting and revoking access, and so on. For authorization, it uses quite fine-grained roles. In
  this area there is a lot of room for improvement (and alternative solutions).

## Design Decisions

Want to know why a certain thing looks like it looks? Check out the [Design Decision](docs/design-decisions) directory.
It contains timestamped documents that explain the reasoning behind various design decisions. You might find links
to them inside JavaDoc comments as well.

Note the temporal nature of the design decisions - they were valid at the time they were written. As this application
evolves, older design decisions will become outdated and overridden by new ones.

## Running the Application

This application uses Testcontainers to start up PostgreSQL when running in development mode and in integration tests.
You must have Docker installed and running to run the application.

Before you do anything else, you have to *make a priming build that generates the jOOQ classes*. Make sure Docker is
installed and running, then run:

```bash
./mvnw package
```

To start the application in development mode, import it into your IDE and run the `TestApplication` class.
You can also start the application from the command line by running: 

```bash
./mvnw
```

You can log in with the user `admin` and password `2smart4u`.