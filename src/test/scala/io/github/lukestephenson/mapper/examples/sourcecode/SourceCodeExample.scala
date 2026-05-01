package io.github.lukestephenson.mapper.examples.sourcecode

import io.github.lukestephenson.mapper.examples.model.FlatUser
import io.github.lukestephenson.mapper.sourcecode.SourceLocation

object SourceCodeExample {
  def main(args: Array[String]): Unit = {
    val myUser = FlatUser("Luke", 30)
    val sourceLocation: SourceLocation = SourceLocation(myUser.age.toLong)
    println(sourceLocation)
  }
}
