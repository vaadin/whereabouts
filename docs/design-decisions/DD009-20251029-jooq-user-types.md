# Design Decision 009: jOOQ User Types

* Recorded: 2025-10-29
* Recorded by: <petter@vaadin.com>

I already mentioned in [DD003](DD003-20251023-value-objects-and-validation.md) why I use value objects in Whereabouts.
This means I also need to convert between database types and the value objects. There are different ways of doing this.

## Database Domain Types

One alternative is to [create the types in the database](https://www.postgresql.org/docs/current/sql-createdomain.html)
and have jOOQ generate them. This reminds me of the two-tiered database applications I used to make with Delphi back in
the late 1990s when I started to learn how to program in my teens. The database contained the data and the constraints
and the UI was talking with the database directly, with the UI matching the database structure completely.

It is still a valid approach for a certain class of applications today. However, the architecture I'm exploring in
Whereabouts does
not belong to that class. Here are the reasons for why I don't want to go down this path:

- I want to keep all the validation logic in one place - the Java source code - and not scattered across multiple
  systems.
- Even if you can impose certain constraints on the database domain types, for instance using regular expressions, you
  sometimes need more advanced validation algorithms, like calculating check digits in bank account numbers or personal
  identity numbers. This is much easier to do in Java.
- One of the points with using validation in the value objects is to catch bad data as early as possible. If validation
  happens in the database, the data has already travelled quite deep into the system and I see this as a security risk.
  I know that with proper escaping nothing bad *should* happen. But nobody thought that just logging a value could be
  harmful either, until Log4Shell happened.
- I want to keep my domain classes and database types decoupled. In many cases they are similar, but I have also
  experienced several use cases where they differ. I want to retain this flexibility, even though it means I have to
  write some extra code (or have an LLM write it for me).
    - One problem with Whereabouts as it is today is that it does not really have a feature that demonstrates the
      benefit of having them separated.
    - Some food for thought: Should the architecture drive the features of Whereabouts, or should the features drive
      the architecture?

## Custom Data Type Converters

Another alternative is to
use [custom data type converters](https://www.jooq.org/doc/latest/manual/sql-execution/fetching/data-type-conversion/).
My first approach involved defining the converters in a separate `JooqConverters` utility classes, like this:

```java
public static final Converter<String, EmailAddress> emailConverter = Converter.ofNullable(
        String.class, EmailAddress.class, EmailAddress::of, EmailAddress::toString
);
```

I would then apply them to fields manually, like this:

```java
private static final Field<EmailAddress> WORK_EMAIL = EMPLOYEE.WORK_EMAIL.convert(emailConverter);
```

My thinking was that using the raw database types in the generated jOOQ classes would give me more flexibility when
using database functions such as SUM.

However, this lead to quite confusing code. Sometimes, I had to refer to my own custom field constants, sometimes
to the generated jOOQ constants. Sometimes the converters were called directly on a value before passing it to the query
as an argument, sometimes they were applied to the fields themselves. The code became difficult to read and understand.

## Forced Types

I then explored forced types, where jOOQ is instructed to apply the converters to the generated field constants
directly. For example, I could force all columns whose names end with `email` to be of the custom type `EmailAddress` by
adding the following definition to the jOOQ generator configuration:

```xml

<forcedType>
    <userType>com.example.whereabouts.common.EmailAddress</userType>
    <converter>com.example.whereabouts.jooq.converters.EmailConverter</converter>
    <includeExpression>^.*email$</includeExpression>
</forcedType>
```

This has the following implications:

- Every converter I use must be a class; I can't use helper methods like `Converter.ofNullable(..)` anymore.
- I have to name my database columns consistently to keep the `includeExpression`s simple.
- I have to define a forced type for every value object.
- I can use the value objects directly in my queries without having to manually convert them.
- If I need to use aggregate functions, I can cast the fields back to their primitive types.

Of all alternatives, I currently believe this is the best one. It requires some extra work up front (creating the
converters and declaring the forced types), but after that you can forget about the converters and focus on your
business domain. Also, with modern AI-driven code completion, it does not take long to put those converters together
and once created, they will rarely change.

## Changes to Package Structure

When creating the converters, I realized I need a better package structure. Should the converters be a part of the
domain model, or a part of the database integration (jOOQ in this case)? At the time of writing, I chose to make all the
converters a part of the database integration and put them into the `com.example.whereabouts.jooq.converters` package,
regardless of which feature package the value objects were coming from.

This in turn made me realize that the way I had been using Spring Modulith previously
(see [DD001](DD001-20251023-package-structure.md)) was wrong. Whereabouts currently consists of a single bounded context
which means the entire project should be one single application module. This in turn means that Spring Modulith is not
needed at all (at least not the structural parts; the moments API and improved domain event support may still be useful
in the future). I therefore ended up removing it form the project. I also removed ArchUnit, as the architecture is still
evolving. Once it has stabilized, I'm considering adding it back.

I also decided to get rid of the `internal` package and move the repositories and queries to separate `repository` and
`query` packages. My goal with this was to make the package structure easier to understand and the code easier to find.
I think more changes to the package structure will happen for the same reason in the near future.