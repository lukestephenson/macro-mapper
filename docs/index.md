---
layout: default
title: macro-mapper
---

# macro-mapper

A Scala 3 macro library for compile-time, type-safe case class mapping.

## Motivation

When working with protobuf-generated code, gRPC services, or any layered architecture, you often need to convert between structurally similar case classes. For example, a protobuf-generated class wraps every field in `Option`, but your domain model uses plain required fields:

```scala
// Generated from protobuf
case class ProtoUser(name: Option[String], age: Option[Int])

// Your domain model
case class User(name: String, age: Int)
```

Writing these conversions by hand is tedious, error-prone, and a source of boilerplate that grows with every new field. **macro-mapper** derives these mappings at compile time, with:

- **No runtime reflection** - all mapping code is generated at compile time
- **Type safety** - if the source is missing a field required by the target, you get a compile error
- **Option unwrapping** - automatically converts `Option[T]` fields to `T`, returning a typed error if the value is `None`
- **Nested support** - recursively maps nested case classes using implicit `Mapper` instances
- **Error tracking** - reports which field path failed, with source location information

## Quick example

```scala mdoc
import io.github.lukestephenson.mapper.{Mapper, MapperMacros, Error}
import io.github.lukestephenson.mapper.Mapper.as

case class ProtoUser(name: Option[String], age: Option[Int])
case class User(name: String, age: Int)

given Mapper[ProtoUser, User] = MapperMacros.derived

val success = ProtoUser(Some("Alice"), Some(30)).as[User]

val failure = ProtoUser(Some("Bob"), None).as[User]
```

Continue to [Installation](installation.md) to get started, or see [Usage](usage.md) for more examples.
