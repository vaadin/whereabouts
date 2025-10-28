# Whereabouts README

## ⚠️ Advanced Reference Architecture - Not for Beginners

This is a **production-grade reference architecture** demonstrating advanced patterns for building
enterprise Vaadin applications. It assumes significant experience with:

- Java and Spring Boot
- Relational databases and SQL
- Domain-Driven Design principles

**New to Vaadin?** Start with our [Getting Started](https://vaadin.com/docs/latest/getting-started)
guide instead. A beginner-friendly tutorial series is coming soon.

This application demonstrates **one opinionated approach** to building Vaadin applications using:

* Vaadin 25
* Spring Boot
* Java 21
* jOOQ
* PostgreSQL

If your application primarily involves straightforward database operations, this example may be
more complex than necessary. Simpler architectural approaches (like database-first development
with JPA) can be more appropriate for those scenarios.

**This reflects specific architectural choices** that may or may not fit your needs. Every major
decision is documented in our [Design Decision Records](docs/design-decisions) with reasoning,
tradeoffs, and alternatives considered.

**Use what makes sense for your context.** Take the patterns you like, adapt what's useful,
ignore the rest. If you're working with a Vaadin consultant or have established patterns,
follow their guidance—they understand your specific requirements better than any example can.

## 🏎️ Running the Application

This application uses Testcontainers to start up PostgreSQL when running in development mode and in integration tests.
You must have Docker installed and running to run the application.

Before you do anything else, you have to **make a priming build that generates the jOOQ classes**. Make sure Docker is
installed and running, then run:

```bash
./mvnw package
```

After this, **you may have to reload the files in your IDE** so that the generated jOOQ classes are picked up.

To start the application in development mode, import it into your IDE and run the `TestApplication` class.
You can also start the application from the command line by running:

```bash
./mvnw
```

You can log in with the user `admin` and password `2smart4u`.

## ⚠️ A Work in Progress

**Whereabouts is not a finished application.** Some features are missing, others only partially implemented. Some
features are polished, others have rough edges. Some code is even experimental and may end up being thrown away.
As we gain new insights, you'll see the application evolve.

**We invite you - the Vaadin community - to participate in this process** by challenging (or agreeing with) the design
decisions in the application, and creating [issues](https://github.com/vaadin/whereabouts/issues)
and [pull requests](https://github.com/vaadin/whereabouts/pulls)!

## 🐶 Dogfooding

This application also serves as an internal testing ground for Vaadin's product team. By building
a realistic application, we discover integration challenges and areas for improvement.

When exploring the source code, you'll notice some things are more
difficult to implement than you'd expect, or that you have to manually build some things that you'd expect would be
provided by the framework. You'll also find different ways of solving the same problem. Over time, as Vaadin evolves,
you'll hopefully see the code improve.
