package io.github.lukestephenson.mapper.examples

import io.github.lukestephenson.mapper.examples.protobuf.{ProtoAddress, ProtoUser}
import io.github.lukestephenson.mapper.{Error, Mapper, MapperMacros}
import io.github.lukestephenson.mapper.Mapper.as
import io.github.lukestephenson.mapper.examples.model.{DeepUser, FlatUser, NiceAddress}

object MapperMacrosExample {
  given Mapper[ProtoUser, FlatUser] = MapperMacros.derived

  given Mapper[ProtoAddress, NiceAddress] = MapperMacros.derived

  given Mapper[ProtoUser, DeepUser] = MapperMacros.derived

  def fromProto(source: ProtoUser): Either[Error, DeepUser] = {
    source.as[DeepUser]
  }

  def main(args: Array[String]): Unit = {
    val address = ProtoAddress(Some("123 Main St"), Some("Springfield"))
    println(fromProto(ProtoUser(Some("John Doe"), Some(30), Some(address))))
  }
}
