package io.github.lukestephenson.mapper

import io.github.lukestephenson.mapper.sourcecode.SourceLocation

import scala.quoted.*

object MapperMacros:
  inline def derived[S, T]: Mapper[S, T] = ${ derivedImpl[S, T]() }

  private def derivedImpl[S: Type, T: Type]()(using Quotes): Expr[Mapper[S, T]] =
    import quotes.reflect.*

    def validate(label: String, tpe: TypeRepr): Unit =
      val classSymbol = tpe.typeSymbol
      if !classSymbol.isClassDef || !classSymbol.flags.is(Flags.Case) then
        report.error(s"$label type ${classSymbol.name} is not a case class")

    val sourceTpe = TypeRepr.of[S]
    val targetTpe = TypeRepr.of[T]
    val targetClassSymbol = targetTpe.typeSymbol
    val sourceClassSymbol = sourceTpe.typeSymbol

    validate("Source", sourceTpe)
    validate("Target", targetTpe)

    val targetParams = targetClassSymbol.primaryConstructor.paramSymss.flatten
    val sourceParams = sourceClassSymbol.primaryConstructor.paramSymss.flatten
    val sourceParamsByName = sourceParams.map(p => p.name -> p).toMap

    def generateBody(value: Expr[S]): Expr[Either[Error, T]] =
      val valueTerm = value.asTerm

      val conversions: List[(Expr[Either[Error, Any]], TypeRepr)] = targetParams.map { targetParam =>
        val name = targetParam.name
        val targetFieldType = targetTpe.memberType(targetParam)
        val conv = sourceParamsByName.get(name) match
          case None =>
            report.error(s"Source does not have field $name required by target")
            '{ Left(Error(s"Source missing field", List.empty)) }
          case Some(_) =>
            val sourceFieldType = sourceTpe.memberType(sourceClassSymbol.fieldMember(name))
            targetFieldType.asType match
              case '[tt] =>
                sourceFieldType.asType match
                  case '[ss] =>
                    val mapperType = TypeRepr.of[Mapper].appliedTo(List(sourceFieldType, targetFieldType))
                    Implicits.search(mapperType) match
                      case success: ImplicitSearchSuccess =>
                        val mapperExpr = success.tree.asExprOf[Mapper[ss, tt]]
                        val fieldSelect = Select.unique(valueTerm, name).asExprOf[ss]
                        '{ $mapperExpr.map($fieldSelect)(using SourceLocation.explicit(${ Expr(name) })) }
                      case failure: ImplicitSearchFailure =>
                        report.error(s"Could not find implicit Mapper[${sourceFieldType.show}, ${targetFieldType.show}] for field $name")
                        '{ Left(Error("No mapper found", List.empty)) }
        (conv, targetFieldType)
      }

      def chainConversions(args: List[(Expr[Either[Error, Any]], TypeRepr)], acc: List[Term]): Expr[Either[Error, T]] =
        args match
          case Nil =>
            val newInstance = New(TypeTree.of[T])
              .select(targetClassSymbol.primaryConstructor)
              .appliedToArgs(acc)
              .asExprOf[T]
            '{ Right($newInstance) }
          case (head, tpe) :: tail =>
            tpe.asType match
              case '[tt] =>
                '{
                  $head.asInstanceOf[Either[Error, tt]].flatMap((v: tt) => ${ chainConversions(tail, acc :+ '{ v }.asTerm) })
                }

      chainConversions(conversions, Nil)

    '{
      new Mapper[S, T] {
        def map(value: S)(using sourceLocation: SourceLocation): Either[Error, T] =
          ${ generateBody('value) }
      }
    }
