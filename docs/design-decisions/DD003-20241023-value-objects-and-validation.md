# Design Decision 003: Value Objects and Validation

* Recorded: 2025-10-23
* Recorded by: <petter@vaadin.com>

Using value objects in your code has three primary benefits:

1. It provides context to your values. Instead of having just a `String`, an `int`, or a `BigDecimal`, you can have an
   `EmailAddress`, a `Quantity`, and a `MonetaryAmount`. Even though the value objects are just wrappers around the
   primitive types, they explain what the primitive value means.
2. It avoids mixing types by accident. If you use strings for phone numbers and email addresses, you can easily mix them
   up. This is especially true in a constructor, where the phone number and email address are next to each other. By
   having them as different value objects, the compiler complains if you pass a `PhoneNumber` as an `EmailAddress`
   parameter.
3. It makes it possible to build validation into the value object, effectively ensuring that a value object cannot even
   be created unless it is syntactically correct.

## Validation in Value Objects

In Whereabouts, there are several examples of value objects that validate or sanitize their inputs in the constructor,
such as:

* `DomainName` (checks that the input is a syntactically valid domain name; does not check that the domain name exists)
* `EmailAddress` (checks that the input is a syntactically valid e-mail address; does not check that the email address
  exists)
* `IpAddress` (checks that the input is a syntactically valid IPv4 or Ipv6 address; does not check that the IP address
  exists)
* `PhoneNumber` (sanitizes and checks that the input is a syntactically valid E.164 telephone number; does not check
  that the phone number exists)

I read the book [Secure by Design](https://www.manning.com/books/secure-by-design) a few years ago, and it convinced me
even more of this pattern. It refers to these validated value objects as "domain primitives". I also use this term, but
people seem to be more familiar with "value objects" than "domain primitives". Anyway, it is a good read.

I also borrowed the validation principles and order from the book:

1. Check the size of the data. If it is too small or too big, reject it.
2. Check the lexical content of the data (applies to text). If it contains any characters that should not be in it,
   reject it.
3. Check the syntax of the data. If the data is not syntactically correct, reject it.

These checks go from cheap to expensive and also provide some (though not complete) protection against using the
validation itself as a Denial-of-Service attack vector. For example, a huge payload that might take down the server when
loaded into memory could be caught by the first size check.

There is a fourth validation step as well, which is semantical validation. This is not something you can do inside a
value object constructor, as it is about checking whether the data makes sense in a specific context. This could be
making sure that a syntactically valid bank account exists and is owned by the person who submitted it.

## Value Objects, Validation, and Vaadin

When using value objects in Vaadin forms, you have to use a `Converter` to convert between the value object and the
wrapped primitive object (like a `String` or an `int`). Any errors thrown by the converter show up as validation errors
in the user interface.

In other words, if you use value objects, the syntactic validation happens in converters, not validators. On the other
hand, you may very well use validators *after* the initial converter to perform *semantic validation*. The application
does not contain any examples of this at the time of writing, but this may change in the future.

## What about Bean Validation?

The traditional way of validating data in Java business applications has been to use Bean Validation annotations and
running the objects through a validator. This is convenient as there are a lot of ready-made constraints that you can
just use. Furthermore, you can group the constraints into various groups, making it possible to run all cheap validators
in a first round and only proceed to more expensive validators if the cheap ones pass. Vaadin also has some built in
support for using Bean Validation directly in forms. Still, I decided not to use them. Why?

My biggest issue with bean validation is that you can't tell whether an object is valid or not by just looking at it.
You always have to run it through a validator and check any constraint violations before you know it is safe. If you
have the validation built into your value objects, you know that the data is at least syntactically valid; otherwise it
would not even exist.

Furthermore, bean validation does not address the context problem, unless you add an `@Email` annotation everywhere. And
you could still accidentally store a phone number in an email address field, although the validator would detect that
during runtime.

In short, I just think using value objects leads to code that is *more robust* and *easier to read* than relying on bean
validation, even though this means I have to write more code. But I personally prefer explicit to implicit or automagic
anyway.

That said, I still think bean validation could be a useful strategy for *semantic validation*. In Spring, you can inject
other Spring beans into constraint validators, and you typically need this to do semantic validation. Again, the
application does not contain any examples of this at the time of writing, but this may change in the future.