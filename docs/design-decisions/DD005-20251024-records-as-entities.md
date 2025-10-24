# Design Decision 005: Records as Entities

* Recorded: 2025-10-24
* Recorded by: <petter@vaadin.com>

Traditionally, I have modeled my entities as mutable POJOs and my value objects as immutable POJOs. I have also put a
lot of my business logic into the entities. The application services were basically just fetching an entity, calling a
business method on it, and saving it back.

This time, I wanted to try out using Java records for the entities as well. The reasons for this were the following:

* Java has gotten more functional programming features, like algebraic data types and pattern matching. This makes it
  possible to utilize types in new ways.
    * Instead of having state flags in your mutable entities, you can have different concrete types for different
      states.
    * For example: a sealed `Order` interface could have three records implementing it:
      `DraftOrder`, `ConfirmedOrder`, `ShippedOrder`. In my services, I could then use interface for operations that
      apply to any order, and the records for operations that apply to orders in a specific state. Granted, this moves
      some business logic out of the entities and into domain services instead, but I don't see this as too big an
      issue.
* I only need to check invariants in a single place: the constructor.
* The immutability makes it safer to use the records as DTOs outside my application layer (such as in the UI). It also
  makes it safer to share entities between threads.
* The risk of stale data showing up somewhere is reduced. You can't accidentally change a field and then have another
  component use that data to make an incorrect business decision.
* I don't use JPA, so I *can* use records as entities.

## Entity Design

What distinguishes an entity from other domain objects is its *identity*. Two entities with the same identity are
considered the same entity. Also, the identity of an entity can never change. You can then ask a question: if an entity
is created without an ID, is it then actually an entity? In other words, does it even make sense to allow an entity to
have a *nullable* ID, as is often the case when using JPA?

I think many things become easier if an entity gets its ID upon creation, so I wanted a design where you can't create
an entity without an ID.

Second, I wanted to use IDs to refer to other entities in a typesafe way. Thus, every entity type would need its own ID
value object.

Third, since the entity is implemented as a record, all its data needs to be available when it is created as well.
Unless our model allows for incomplete entities, this data must be valid and meet all the entity's invariants. Thus, my
design should not allow entity creation without all the data the entity needs.

Fourth, I wanted to be able to use a Vaadin form and `Binder` to get this data from the user.

These four requirements led me to a design where every entity requires three types:

1. An ID type wrapping either a long, a UUID or a string. Example: `EmployeeId`
2. A data type containing only the data of the entity, not the identity. This data type could be instantiated before the
   entity is created and used directly in `Binder` as a form data object. Example: `EmployeeData`
3. An entity type containing the ID, the data, and any other metadata such as an optimistic locking version. Example:
   `Employee`

In code, the entity looks like this:

```java
public record Employee(
        EmployeeId id,
        long optimisticLockingVersion,
        EmployeData data
) implements Entity<EmployeeId> {

    public Employee {
        // Enforce invariants, for example like this:
        Objects.requireNonNull(id);
        Objects.requireNonNull(data);
    }
}
```

This allows you to create repository methods like this:

```java
interface EmployeeRepository extends Repository {

    // When this method is called, the entity does not exist yet, only the data. The repository generates a new ID,
    // persists the entity, returns a new Employee instance with the ID, data, and optimistic locking version 1.
    Employee insert(EmployeeData data);

    // When this method is called, the entity exists. The repository needs both the ID (to know which entity to update)
    // and the optimistic locking version (to know whether the entity has been touched by another user), and so it makes
    // sense to pass in the full Employee object. After successfully updating the database, the repository returns
    // a new Employee instance with an incremented optimistic locking version.
    Employee update(Employee employee);
}
```

For the `update` method to work, you need a way of replacing the data object with a new one, while keeping the ID and
optimistic locking version intact. You can do this with a *wither*:

```java
public record Employee(
        EmployeeId id,
        long optimisticLockingVersion,
        EmployeData data
) implements Entity<EmployeeId> {

    public Employee withData(EmployeeData newData) {
        return new Employee(id, optimisticLockingVersion, newData);
    }
}
```

## Drawbacks

Needing to create three types for one entity clutters your packages. You might get away with declaring the data type and
the identity type as inner types of the entity types, but this could lead to encapsulation problems further down the
road. Although the types belong to the same entity, they have distinct roles and are intended to be used in different
scenarios. I think keeping them as separate high-level types gives you more degrees of freedom for future evolutions.

Changing individual properties while keeping everything else unchanged is difficult as you have to create a new record
for each change. You could create more *withers* or use the builder pattern, but this means more boilerplate code for
you to write. If you don't like boilerplate, you can use an LLM to write the code for you, find some third-party code
generator, or wait for [JEP-468](https://bugs.openjdk.org/browse/JDK-8321133).

Since a jOOQ result set is strongly typed, you can easily map a database row to a Java record by using
`Records.mapping(MyRecordType::new)`. This is no longer possible with a two-level entity structure, as some fields in
the result should go into the entity (ID, optimistic locking version) while the rest should go into the data object. To
me, this is just one of those situations where you can't have it all: you can make it easier to implement your
repositories, while making it harder to implement your business logic and user interface, or vice versa. Given that you
will spend most of your time working with the business logic and the UI, I think it is acceptable to optimize the API
for those areas and accept a little extra work in your persistence layer.
