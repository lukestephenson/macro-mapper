package io.github.lukestephenson.mapper

import io.github.lukestephenson.mapper.sourcecode.SourceLocation

case class Error(message: String, path: List[SourceLocation])
