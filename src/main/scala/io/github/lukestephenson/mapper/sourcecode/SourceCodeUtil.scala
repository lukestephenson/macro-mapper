package io.github.lukestephenson.mapper.sourcecode

import scala.quoted.{Expr, Quotes}

object SourceCodeUtil {
  def sourceCode(expr: Expr[Any])(using quotes: Quotes): Expr[String] = {
    Expr(expr.show)
  }
}
