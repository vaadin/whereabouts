# Design Decision 004: Nullability

* Recorded: 2025-10-24
* Recorded by: <petter@vaadin.com>

Many bugs in Java applications are caused by `NullPointerException`s. Many of them could have been easily avoided from
the start by being more explicit about nullability in parameters, fields, and return values. In Whereabouts, I'm
trying to achieve this by using `null` in a consistent manner:

* Only use `null` for parameters and fields that are *optional*.
* Only getter methods can return `null`; other methods return `Optional`.
* Don't use `null` as a state, like determining whether an entity is persisted by checking whether it has a
  non-null ID.

Furthermore, I use [JSpecify](https://jspecify.dev/) nullability annotations:

* All classes are annotated with `@NullMarked`, meaning all parameters, fields, and return values are assumed to never
  be null unless annotated otherwise.
    * It is also possible to mark an entire package as `@NullMarked`, but then you need a `package-info.java` file in
      every package. Originally I was trying to avoid this to avoid cluttering the packages, but in hindsight,
      package-level annotations might actually make the code itself less cluttered.
* I mark nullable elements with `@Nullable`.

## Problems

There are a few problems with JSpecify and especially Vaadin. Vaadin is not as clear about nullability as I
would want it to be, nor does it use any nullability annotations. Some methods accept null parameters, others don't.
Some methods can return null values, others don't. Some methods have information about nullability in their JavaDocs,
others don't (and then you have to check the source code to find out).

The problem with using `@NullMarked`, at least in IntelliJ IDEA, is that it assumes to everything in the code that is
not explicitly marked as `@Nullable`, is never null. This includes results from third-party libraries like Vaadin,
jOOQ, and Spring. Thus, you can run into cases where IDEA claims a return value can never be null, even though you
know for a fact that it can be. Then you have to start overriding things with `@NullUnmarked` annotations or
`//noinspection ConstantValue` comments.

Another problem with the JSpecify annotations is that sometimes, the nullability of the return value of a method depends
on the input parameters. You could, for instance, have a method that returns null if and only if its input parameter is
null. Marking such a method as `@Nullable` only tells half the story, because the IDE would direct you into checking
the nullability of the return value even though you might know for sure it can never be null because the input parameter
is never null. JetBrains annotations include a `@Contract` annotation for this, and it would be great if JSpecify got
one as well.

One solution to the problems could be to explicitly mark everything as either `@Nullable` or `@NonNull` when that is
always the case, and leave out the annotation in cases where something is sometimes nullable and sometimes not. However,
this would clutter the code with annotations. At the time of writing, I think aiming for a design where non-null is the
default and null the exception is a better long-term solution.
