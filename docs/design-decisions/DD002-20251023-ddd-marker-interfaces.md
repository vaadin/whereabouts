# Design Decision 002: Domain-Driven Design Marker Interfaces

* Recorded: 2025-10-23
* Recorded by: <petter@vaadin.com>

I have been a fan of [domain-driven design (DDD)](https://martinfowler.com/bliki/DomainDrivenDesign.html) for over 10
years. Even in projects that are not actually domain-driven, I often end up borrowing some concepts from DDD because
I think they make the code better. Sometimes, it is strategic concepts (like bounded contexts), other times it is
tactical concepts (like value objects, entities, aggregates, and repositories).

This application makes heavy use of the following strategic concepts:

* Value objects
* Entities
* Repositories

## What is a Value Object?

A value object is an immutable Java object whose *equality is based on its value*. In other words, if two objects have
the same value, they can be considered the same value object.

## What is an Entity?

An entity is a Java object whose *equality is based on its identity*. To do this, each entity needs a unique
*ID* (which is often a value object). Two entities of the same class, with the same ID, are considered the same entity
even though they contain different data. Likewise, two entities with otherwise identical data, but different IDs, are
not considered the same entity (just as two people with the same names are not considered the same person).

## What is a Repository?

A repository is a storage of entities (actually, it is a storage of
[aggregates](https://martinfowler.com/bliki/DDD_Aggregate.html), but I decided to leave the aggregate concept out of
this example application for now to keep things simple).

You can use a repository to create new entities, retrieve entities by their IDs, and update and delete entities. The
implementation of a repository typically uses a database of some kind to store the entities.

## Marker Interfaces

To make the role of a specific domain object clearer, I have declared marker interfaces for the objects to implement.
This is similar to what [jMolecules](https://github.com/xmolecules/jmolecules) does, but I prefer to make my own
interfaces just because they are easier to tweak to my needs (and you have one less dependency to maintain).

I created the following marker interfaces:

* `ValueObject` - an empty interface that should be implemented by all value objects.
* `Identifier` - an empty interface that extends `ValueObject` and should be implemented by value objects that are used
  as IDs (such as `EmployeeId`, `ProjectId`, etc.)
* `Entity` - an interface with a single `id()` method that returns the ID of the entity.
* `Repository` - an empty interface that should be implemented by repositories.

I want to open up the reasoning behind the `Identifier`, `Entity`, and `Repository` interfaces a little bit further.

### Identifier

If you have a JPA background, you're used to referring to other entities using their classes. This has its benefits,
but also some drawbacks. You may end up loading a whole chain of entities that you don't need (unless you use lazy
loading, which has its own quirks) or worse - you might end up editing an entity that you did not intend to edit.

In this application, I have instead opted to refer to other entities using their identifiers. I could do this with
`long` or `UUID`, but then I would lose the type support. The compiler would not detect if I accidentally stored the
ID of an `Employee` inside a `projectId` field. To fix this, I've created dedicated ID types for every entity.

For example, instead of storing a `Project` reference inside the `Task` entity, I use a `ProjectId` reference. I can
then use this ID to look up the corresponding `Project` from its repository *if and when I need it*.

### Entity

As having an identity is central to the entity concept, it makes sense to force all entities to have a method that
returns the ID. The return type of this method is a generic parameter that has to extend `Identifier`, effectively
forcing you to create ID types for every entity. In some cases, you can actually share the same ID type with several
entities. One-to-one relationships between the entities is the first case that comes to mind.

I've chosen to name the getter method `id()` and not `getId()` because I want to use the `Entity` interface with Java
records, and then I just have to declare an `id` record component with the correct type.

It would make sense to also provide implementations for `equals()` and `hashCode()` that use the identity, but you
can't put those in an interface, and if you put them in an abstract base class, you can't use records.

### Reposistory

The `Repository` interface is currently an empty marker interface. It did not start like that. My first version
contained typical repository methods, like `getById(..)`, `insert(..)`, `update(..)`, `delete(..)`, and `contains(..)`.
This worked for a while.

In most cases, the insert operation created a new ID, and the API was designed as such. However, I soon ran into a case
where I needed to insert an entity with an ID that was already known (the one-to-one relationship I mentioned earlier).
I now had two options: change my entity to use a surrogate key, or change the repository interface. I chose the latter.

I originally added the methods to the repository interface to make repositories easier to implement and more consistent.
However, given how the application is structured, no repository will ever be accessed through the generic `Repository`
interface. From that point of view, declaring the methods in that repository makes no sense.

Furthermore, it turned out not all application services needed all repository methods. Since I'm implementing the
repositories myself, using jOOQ, I would have to either implement methods that I *might need* in the future, or have
them throw `UnsupportedOperationException`. And I think having to throw an `UnsupportedOperationException` is a
symptom of a failed API design.

And that is how `Repository` ended up an empty marker interface. I can use it in ArchUnit tests to make sure that e.g.
all public repository methods are using the `@Transactional` interface, but I'm free to define (and implement) only
the repository methods I need for *every* repository.
