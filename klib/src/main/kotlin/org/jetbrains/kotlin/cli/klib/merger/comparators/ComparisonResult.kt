package org.jetbrains.kotlin.cli.klib.merger.comparators

sealed class ComparisonResult

class Success : ComparisonResult()
class Failure(val causes: List<Cause>) : ComparisonResult() {
    constructor(cause: Cause) : this(listOf(cause))
}

infix fun ComparisonResult.and(other: ComparisonResult): ComparisonResult {
    return when (this) {
        is Success -> other
        is Failure -> when (other) {
            is Failure -> Failure(this.causes + other.causes)
            is Success -> this
        }
    }
}

fun ComparisonResult.map(init: (List<Cause>) -> Cause): ComparisonResult {
    return when (this) {
        is Success -> Success()
        is Failure -> Failure(init(this.causes))
    }
}

fun Boolean.toResult(cause: Cause) = if (this) Success() else Failure(cause)

sealed class Cause

object Unknown : Cause()

object ReturnType : Cause() // TODO ???
object FqName : Cause()
object Suspend : Cause()
object Actual : Cause()
object Expect : Cause()
object External : Cause()

object LateInit : Cause()
object Const : Cause()
object ClassKind : Cause()
object Type : Cause()
object IsVar : Cause()
object Visibility : Cause()
object Size : Cause()


sealed class ComplexCause(val cause: List<Cause>) : Cause()

class Parameter(causes: List<Cause>, val index: Int = -1) : ComplexCause(causes)
class ParametersMismatched(causes: List<Parameter>) : ComplexCause(causes)

class Function(causes: List<Cause>) : ComplexCause(causes)
class Class(causes: List<Cause>) : ComplexCause(causes)
class TypeAlias(causes: List<Cause>) : ComplexCause(causes)
class Value(causes: List<Cause>) : ComplexCause(causes)

