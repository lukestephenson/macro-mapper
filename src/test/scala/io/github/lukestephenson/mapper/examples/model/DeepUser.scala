package io.github.lukestephenson.mapper.examples.model

case class NiceAddress(street: String, city: String)

case class DeepUser(name: String, age: Int, address: NiceAddress)
