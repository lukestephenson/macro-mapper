---
layout: default
title: Usage
---

# Usage

## Basic mapping

For case classes with matching field names and types, derive a mapper with `MapperMacros.derived`:

```scala mdoc
import io.github.lukestephenson.mapper.{Mapper, MapperMacros, Error}
import io.github.lukestephenson.mapper.Mapper.as

case class Source(name: String, age: Int)
case class Target(name: String, age: Int)

given Mapper[Source, Target] = MapperMacros.derived

Source("Alice", 30).as[Target]
```

## Option unwrapping

When the source has `Option[T]` fields and the target has `T` fields, the mapper automatically unwraps the option. If a value is `None`, it returns an `Error` instead:

```scala mdoc
case class ProtoEvent(title: Option[String], capacity: Option[Int])
case class Event(title: String, capacity: Int)

given Mapper[ProtoEvent, Event] = MapperMacros.derived

ProtoEvent(Some("Scala Days"), Some(500)).as[Event]

ProtoEvent(Some("Scala Days"), None).as[Event]
```

## Nested case classes

Mappers compose. If you have a `Mapper[A, B]` in scope, it will be used when mapping fields of type `A` to `B` in an outer class:

```scala mdoc
case class ProtoAddress(street: Option[String], city: Option[String])
case class Address(street: String, city: String)

case class ProtoCustomer(name: Option[String], address: Option[ProtoAddress])
case class Customer(name: String, address: Address)

given Mapper[ProtoAddress, Address] = MapperMacros.derived
given Mapper[ProtoCustomer, Customer] = MapperMacros.derived

val proto = ProtoCustomer(
  Some("Bob"),
  Some(ProtoAddress(Some("123 Main St"), Some("Springfield")))
)
proto.as[Customer]
```

When a nested optional is `None`, the error path tells you which field failed:

```scala mdoc
val missing = ProtoCustomer(Some("Bob"), None)
missing.as[Customer]
```

## Error handling

The `as` extension method returns `Either[Error, T]`. The `Error` contains a message and a path showing which field(s) caused the failure:

```scala mdoc
case class Proto(x: Option[String], y: Option[Int], z: Option[Boolean])
case class Domain(x: String, y: Int, z: Boolean)

given Mapper[Proto, Domain] = MapperMacros.derived

val result = Proto(Some("hello"), None, Some(true)).as[Domain]
result.left.map(e => (e.message, e.path.map(_.toString)))
```

## Compile-time safety

If the source case class is missing a field required by the target, the macro reports a compile error. For example, the following would not compile:

```scala
// This would NOT compile:
// case class Incomplete(name: String)
// case class Full(name: String, age: Int)
// given Mapper[Incomplete, Full] = MapperMacros.derived
// Error: Source does not have field age required by target
```

Similarly, if no implicit `Mapper` exists for a field type conversion, you get a compile error:

```scala
// This would NOT compile:
// case class A(value: Int)
// case class B(value: String)
// given Mapper[A, B] = MapperMacros.derived
// Error: Could not find implicit Mapper[Int, String] for field value
```
