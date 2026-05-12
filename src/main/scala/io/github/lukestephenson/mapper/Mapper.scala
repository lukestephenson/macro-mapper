package io.github.lukestephenson.mapper

import io.github.lukestephenson.mapper.sourcecode.SourceLocation

trait Mapper[T, S] {
  def map(value: T)(using sourceLocation: SourceLocation): Either[Error, S]
}

object Mapper {
  given idMapper[A]: Mapper[A, A] with {
    def map(value: A)(using sourceLocation: SourceLocation): Either[Error, A] = Right(value)
  }

  given optionMapper[T, S](using mapper: Mapper[T, S]): Mapper[Option[T], S] with {
    def map(value: Option[T])(using sourceLocation: SourceLocation): Either[Error, S] = value match {
      case Some(v) => mapper.map(v).left.map(error => error.copy(path = sourceLocation :: error.path))
      case None => Left(Error("Unable to find value.", List(sourceLocation)))
    }
  }

  given listMapper[T, S](using mapper: Mapper[T, S]): Mapper[List[T], List[S]] with {
    def map(value: List[T])(using sourceLocation: SourceLocation): Either[Error, List[S]] = value.foldLeft[Either[Error, List[S]]](Right(List.empty)) { case (acc, v) =>
      acc match {
        case Left(error) => Left(error)
        case Right(list) =>
          // TODO list index in error
          mapper.map(v).fold(error => Left(error.copy(path = sourceLocation :: error.path)), mapped => Right(mapped :: list))
      }
    }
  }

  extension [T](inline value: T) {
    inline def as[S](using mapper: Mapper[T, S]): Either[Error, S] = {
      given SourceLocation = SourceLocation(value)

      mapper.map(value)
    }
  }
}
