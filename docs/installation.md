---
layout: default
title: Installation
---

# Installation

macro-mapper is published to Maven Central for Scala 3.

## sbt

```scala
libraryDependencies += "io.github.lukestephenson" %% "macro-mapper" % "@VERSION@"
```

## Mill

```scala
ivy"io.github.lukestephenson::macro-mapper:@VERSION@"
```

## Scala CLI

```scala
//> using dep io.github.lukestephenson::macro-mapper:@VERSION@
```

## Requirements

- **Scala 3.3+** (uses Scala 3 quoted macros)
- **cats-core** is a transitive dependency (used for standard type classes)

## Imports

The core imports you'll need:

```scala mdoc:silent
import io.github.lukestephenson.mapper.Mapper
import io.github.lukestephenson.mapper.MapperMacros
import io.github.lukestephenson.mapper.Error
import io.github.lukestephenson.mapper.Mapper.as
```
