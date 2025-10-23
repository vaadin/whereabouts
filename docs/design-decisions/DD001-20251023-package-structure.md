# Design Decision 001: Package Structure

* Recorded: 2025-10-23
* Recorded by: <petter@vaadin.com>

Most business applications I have written during my career have used a layer-based package structure, where code has
been placed in packages according to which "layer" it belongs to. In the beginning, you would fine packages like
`services`, `domain`, `util`, `views` (or `ui`), etc. Later, as I started to embrace
the [ports and adapters](https://alistair.cockburn.us/hexagonal-architecture)
architecture, you'd also find packages like `ports` and `adapters`, with corresponding subpackages.

This structure makes the architecture of the application quite apparent; by looking at the code you can see how the
application is structured in a high level. But it also scatters features across many different packages, which can make
them more difficult to change. It also risks introducing unintended coupling between unrelated features.

## Feature-Based Packages

This time, I wanted to go for a feature-based package structure, to keep classes that implement the same feature closer
together. But what constitutes a feature? On one end of the spectrum, a feature could be a
[bounded context](https://martinfowler.com/bliki/BoundedContext.html). On the other end, it could be a UI view or a
group of views. This example application is not big enough to contain multiple bounded contexts (although it might at
some point), so I ended up with two feature packages:

* `humanresources` for office locations and employees
* `projects` for projects and tasks

I also needed a `common` package with common classes used by all the other feature packages, and a `security` package
with security-related classes, like role constants, the `UserId` value object, the Spring Security configuration and so
on.

## Component Thinking

After being introduced to the [C4 model](https://c4model.com/) and starting to like it, I've started to switch my
architectural thinking from layers to *components*. Component is a saturated term in our industry, but this is how I
think of a component:

- A component can be *instantiated*.
- A component can have a *public API* that other components can *call* (but this is not a requirement).
- A component can have a *public SPI* that other components can *implement* (but this is not a requirement).
- A component can be *replaced* with another one that has the same API.

This way of thinking can be applied to all layers of your application, and not only to the user interface, where Vaadin
already has components with both APIs and SPIs (e.g. the Data Providers). Any application is a collection of components
that interact with each other through APIs and SPIs. By structuring or naming your components in a certain way, you can
build a layered or a ports-and-adapters architecture from components with APIs and SPIs. I argue that a component-based
way of looking at architecture is actually a superset of the more commonly used architectural styles.

## Components or Modules?

Java does not have a built-in "component" concept. Following the component definition above, most components would
actually be classes in Java. Declaring every component inside its own package would not be practical. Also, as the term
is saturated, it would be a source of confusion to talk about components everywhere - especially in a Vaadin application
where most people associate the term with UI components (I've tried and failed). We need another concept.

A related term that comes to mind is *module*. I've sometimes seen the terms module and component used interchangeably,
but I think this is wrong. In my view, a module is a *collection of related code*. You typically don't instantiate a
module. A module can *contain* classes (or components) that can be instantiated. Which means that a module can contain
public APIs and SPIs that are then used by classes that may not even be visible outside the module. Now we are
approaching how [Spring Modulith](https://spring.io/projects/spring-modulith) reasons about package structures.

## Spring Modulith

Here is a short recap of what a Spring Modulith application is: it is an application based on application modules,
where each application module has a clearly defined API that it exposes to other modules. Classes that are not part of
the API are internal and cannot be called from other modules. You can then verify that your application sticks to these
rules, by making sure no internal classes are called outside the module, or that there are no circular dependencies
between modules. Spring Modulith provides a lot of other features as well, but in the context of package structure
this is all you need to know.

*Simple application modules* consist of a single package, where the public classes are the API (or SPI) and the package
private classes are internal. Here, the Java compiler helps you to enforce the architectural constraints. I like it
when the Java compiler helps me keep my code in shape, so I considered this approach at first. But it soon became clear
that you want to at least keep the UI code and the application code separate. Fortunately, Spring Modulith has a
solution for this as well - *advanced application modules*.

An advanced application module consists of multiple packages. By default, the root package is the API package and all
the subpackages are internal. You can override this with annotations. However, now you have to make some internal
classes and interfaces public and so the compiler can't prevent you from misusing them anymore. Instead, you have to
rely on architectural unit tests to make sure internal classes remain internal even though they are public. You can
use [ArchUnit](https://www.archunit.org/) or Spring Modulith's own structure verification mechanisms to do this. In
practice, it means that you write tests that check the structure of your application and fail the build if you e.g.
depend on an internal class outside its module.

Inspired by this, I ended up with a feature package structure that looks like this:

* Application services form the primary API for the feature and so end up in the root package. Any DTOs, value objects,
  entities, enums, etc. that the services expose also end up in the root package.
* The Vaadin UI ends up in a `ui` subpackage that is internal. The UI classes also have package visibility by default,
  as you should not need to import them anywhere else.
* Repositories and data access code should not be exposed outside the feature package either, so they are inside an
  `internal` subpackage. Because they are used by the application services in the root package, they need to be public.

I even added Spring Modulith to the application to try out the module verification mechanism in my architecture unit
test, and it works. However, I'm not sure Spring Modulith should be used like this, as I feel the granularity might be
a bit off. To my understanding, Spring Modulith has been planned with bounded contexts in mind, where each application
module forms its own bounded context. I already concluded this example application has only one bounded context, so you
could argue that it should consist of a single application module only. That said, it works, and it helped me track
down some circular dependencies during the development of the application.

## Problems

This package structure is not without problems and there are especially two issues I want to highlight in this document.

### Mixing Domain and Service Classes

The first issue is whether you should mix domain classes and application services in the same package. Looking at e.g.
the ports and adapters architecture, the domain model and the application services are different things. The domain
model is at the core of the application, and then you have application services around it that act as ports to the
outside world. The application services are also responsible for handling cross-cutting concerns like managing database
transactions and security.

Furthermore, if you are doing domain-driven design, you also have domain services which are different from application
services. If you have everything in the same package, you could differentiate between the two by making application
services public and domain services package private, but it could still be confusing.

I can see at least two valid cases for having the domain model classes in its own subpackage: if the root package grows
too large, or if you want to protect the domain model.

If you are splitting up the root package because it became too large, the domain subpackage would probably still be
public, and you would still be able to use domain classes in your API.

If you are splitting up the root package to protect your domain model, the domain subpackage would be internal, and your
application service would have to use DTOs. However, in my experience, value objects and domain events are useful to
expose as a part of the API. Thus, these classes would have to be in one package, and the internal domain classes
(entities, repositories, domain services, etc.) in another.

### Orphaned Classes

The second issue is that with this structure it is difficult to find a suitable package for certain classes.

Where would you, for instance, put your Vaadin main layout, your Vaadin error handler or any other
`VaadinServiceInitListener`? Right now, these classes are in the application's root package, next to the `Application`
class. I'm not at all sure this is the right place for them.

I've also tried having them in the `common` feature package, but that also felt wrong as that package is supposed to
contain reusable classes and not components that are instantiated on their own. Furthermore, you often need to refer
the main layout in various Vaadin annotations, so it has to be publicly accessible to all other feature packages. At
the time of writing, *I have no good solution for this*.

The `security` package is another problem, or more specifically, *will become* another problem. At the time of writing,
the security model of the application is very trivial, but this will change in the future. As more security features
are added, it will become quite difficult to avoid circular dependencies between the `security` and `common` packages.

Most likely, some security related value objects, like a `UserId` or `UserReference` or even `Permission` would need to
be moved to the `common` package, which opens the discussion of what role a `security` package would need to play in
the first place. But that is another design decision.
