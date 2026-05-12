package io.github.lukestephenson.mapper

import io.github.lukestephenson.mapper.Mapper.as
import io.github.lukestephenson.mapper.sourcecode.SourceLocation

class AdtMapperTest extends munit.FunSuite {

  enum ExampleSourceAdt {
    case Empty
    case SourceLong(value: Long)
    case SourceString(value: String)
  }

  enum ExampleTargetAdt {
    case TargetLong(value: Long)
    case TargetString(value: String)
  }

  implicit val adtMapper: Mapper[ExampleSourceAdt, ExampleTargetAdt] with {
    def map(value: ExampleSourceAdt)(using sourceLocation: SourceLocation): Either[Error, ExampleTargetAdt] = {
      val x = value match {
        case Empty => Left(Error("Empty", sourceLocation))
        case SourceLong(value) => Right(TargetLong(value))
        case SourceString(value) => Right(TargetString(value))
      }
    x
  }

  test("maps adts") {
    val source = List(ExampleSourceAdt.SourceLong(2), ExampleSourceAdt.SourceString("hello"))

    val result = source.as[List[ExampleTargetAdt]]
    assertEquals(result, Right(List(ExampleTargetAdt.TargetLong(2), ExampleTargetAdt.TargetString("hello"))))
  }
}
