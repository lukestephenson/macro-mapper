package io.github.lukestephenson.mapper

import io.github.lukestephenson.mapper.Mapper.as
import io.github.lukestephenson.mapper.sourcecode.SourceLocation

class MapperMacrosTest extends munit.FunSuite {

  case class Source(name: String, age: Int)
  case class Target(name: String, age: Int)

  case class OptSource(name: Option[String], age: Option[Int])
  case class PlainTarget(name: String, age: Int)

  case class InnerSource(street: Option[String])
  case class InnerTarget(street: String)

  case class OuterSource(label: String, inner: InnerSource)
  case class OuterTarget(label: String, inner: InnerTarget)

  case class ProtoStyle(name: Option[String], age: Option[Int], inner: Option[InnerSource])
  case class DomainStyle(name: String, age: Int, inner: InnerTarget)

  case class ListSource(value: List[Option[String]])
  case class ListTarget(value: List[String])

  given Mapper[Source, Target] = MapperMacros.derived
  given Mapper[OptSource, PlainTarget] = MapperMacros.derived
  given Mapper[InnerSource, InnerTarget] = MapperMacros.derived
  given Mapper[OuterSource, OuterTarget] = MapperMacros.derived
  given Mapper[ProtoStyle, DomainStyle] = MapperMacros.derived

  test("maps matching case classes with identical field types") {
    val source = Source("Alice", 30)
    val result = source.as[Target]
    assertEquals(result, Right(Target("Alice", 30)))
  }

  test("unwraps Option fields when all values are present") {
    val source = OptSource(Some("Bob"), Some(25))
    val result = source.as[PlainTarget]
    assertEquals(result, Right(PlainTarget("Bob", 25)))
  }

  test("returns error when an Option field is None") {
    val source = OptSource(None, Some(25))
    val result = source.as[PlainTarget]
    assert(result.isLeft)
    val error = result.left.toOption.get
    assertEquals(error.message, "Optional value was not present.")
  }

  test("error path includes the field name for None option") {
    val source = OptSource(Some("Charlie"), None)
    val result = source.as[PlainTarget]
    assert(result.isLeft)
    val error = result.left.toOption.get
    assert(error.path.nonEmpty, "error path should not be empty")
    assert(error.path.exists(_.toString.contains("age")), s"error path should reference 'age', got: ${error.path}")
  }

  test("maps nested case classes") {
    val source = OuterSource("home", InnerSource(Some("123 Main St")))
    val result = source.as[OuterTarget]
    assertEquals(result, Right(OuterTarget("home", InnerTarget("123 Main St"))))
  }

  test("returns error for nested case class with missing field") {
    val source = OuterSource("home", InnerSource(None))
    val result = source.as[OuterTarget]
    assert(result.isLeft)
  }

  test("maps deeply nested proto-style to domain-style") {
    val source = ProtoStyle(Some("Dana"), Some(40), Some(InnerSource(Some("456 Elm St"))))
    val result = source.as[DomainStyle]
    assertEquals(result, Right(DomainStyle("Dana", 40, InnerTarget("456 Elm St"))))
  }

  test("returns error when outer optional is None in proto-style") {
    val source = ProtoStyle(Some("Eve"), Some(35), None)
    val result = source.as[DomainStyle]
    assert(result.isLeft)
  }

  test("returns error when inner optional is None in proto-style") {
    val source = ProtoStyle(Some("Frank"), Some(28), Some(InnerSource(None)))
    val result = source.as[DomainStyle]
    assert(result.isLeft)
  }

  test("identity mapping preserves values") {
    val source = Source("Grace", 50)
    given Mapper[Source, Source] = MapperMacros.derived
    val result = source.as[Source]
    assertEquals(result, Right(Source("Grace", 50)))
  }

  test("error path includes the index for list failures") {
    given Mapper[ListSource, ListTarget] = MapperMacros.derived
    val source = ListSource(List(Some("Charlie"), None))
    val result = source.as[ListTarget]
    assert(result.isLeft)
    val error = result.left.toOption.get
    assert(error.path.nonEmpty, "error path should not be empty")
    assert(error.path == List(SourceLocation.explicit("value[1]")))
  }
}
