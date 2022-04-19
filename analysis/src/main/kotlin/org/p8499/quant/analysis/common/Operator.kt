package org.p8499.quant.analysis.common

fun Double?.finiteOrNull(): Double? = if (this?.isFinite() == true) this else null

operator fun Double?.plus(that: Double?): Double? = let(this, that) { a, b -> a + b }
operator fun Double?.minus(that: Double?): Double? = let(this, that) { a, b -> a - b }
operator fun Double?.times(that: Double?): Double? = let(this, that) { a, b -> a * b }
operator fun Double?.div(that: Double?): Double? = let(this, that) { a, b -> (a / b).finiteOrNull() }

infix fun Double?.between(range: ClosedRange<Double>): Double? = this?.let { if (range.contains(it)) 1.0 else 0.0 }

infix fun Double?.lt(that: Double?): Double? = let(this, that) { a, b -> if (a < b) 1.0 else 0.0 }
infix fun Double?.le(that: Double?): Double? = let(this, that) { a, b -> if (a <= b) 1.0 else 0.0 }
infix fun Double?.eq(that: Double?): Double? = let(this, that) { a, b -> if (a == b) 1.0 else 0.0 }
infix fun Double?.ge(that: Double?): Double? = let(this, that) { a, b -> if (a >= b) 1.0 else 0.0 }
infix fun Double?.gt(that: Double?): Double? = let(this, that) { a, b -> if (a > b) 1.0 else 0.0 }

infix fun Double?.and(that: Double?): Double? = let(this, that) { a, b -> if (a > 0.0 && b > 0.0) 1.0 else 0.0 }
infix fun Double?.or(that: Double?): Double? = let(this, that) { a, b -> if (a > 0.0 || b > 0.0) 1.0 else 0.0 }

operator fun List<Double?>.plus(that: List<Double?>): List<Double?> = mapIndexed { i, value -> value + that[i] }
operator fun List<Double?>.minus(that: List<Double?>): List<Double?> = mapIndexed { i, value -> value - that[i] }
operator fun List<Double?>.times(that: List<Double?>): List<Double?> = mapIndexed { i, value -> value * that[i] }
operator fun List<Double?>.div(that: List<Double?>): List<Double?> = mapIndexed { i, value -> value / that[i] }

operator fun List<Double?>.plus(that: Double?): List<Double?> = map { it + that }
operator fun List<Double?>.minus(that: Double?): List<Double?> = map { it - that }
operator fun List<Double?>.times(that: Double?): List<Double?> = map { it * that }
operator fun List<Double?>.div(that: Double?): List<Double?> = map { it / that }

operator fun MutableList<Double?>.plusAssign(that: List<Double?>): Unit = forEachIndexed { i, value -> this[i] = value + that[i] }
operator fun MutableList<Double?>.minusAssign(that: List<Double?>): Unit = forEachIndexed { i, value -> this[i] = value - that[i] }
operator fun MutableList<Double?>.timesAssign(that: List<Double?>): Unit = forEachIndexed { i, value -> this[i] = value * that[i] }
operator fun MutableList<Double?>.divAssign(that: List<Double?>): Unit = forEachIndexed { i, value -> this[i] = value / that[i] }

operator fun MutableList<Double?>.plusAssign(that: Double?): Unit = forEachIndexed { i, value -> this[i] = value + that }
operator fun MutableList<Double?>.minusAssign(that: Double?): Unit = forEachIndexed { i, value -> this[i] = value - that }
operator fun MutableList<Double?>.timesAssign(that: Double?): Unit = forEachIndexed { i, value -> this[i] = value * that }
operator fun MutableList<Double?>.divAssign(that: Double?): Unit = forEachIndexed { i, value -> this[i] = value / that }

infix fun List<Double?>.between(range: ClosedRange<Double>): List<Double?> = map { it between range }

infix fun List<Double?>.lt(that: List<Double?>): List<Double?> = mapIndexed { i, value -> value lt that[i] }
infix fun List<Double?>.le(that: List<Double?>): List<Double?> = mapIndexed { i, value -> value le that[i] }
infix fun List<Double?>.eq(that: List<Double?>): List<Double?> = mapIndexed { i, value -> value eq that[i] }
infix fun List<Double?>.ge(that: List<Double?>): List<Double?> = mapIndexed { i, value -> value ge that[i] }
infix fun List<Double?>.gt(that: List<Double?>): List<Double?> = mapIndexed { i, value -> value gt that[i] }

infix fun List<Double?>.lt(that: Double?): List<Double?> = map { it lt that }
infix fun List<Double?>.le(that: Double?): List<Double?> = map { it le that }
infix fun List<Double?>.eq(that: Double?): List<Double?> = map { it eq that }
infix fun List<Double?>.ge(that: Double?): List<Double?> = map { it ge that }
infix fun List<Double?>.gt(that: Double?): List<Double?> = map { it gt that }

infix fun Double?.lt(that: List<Double?>): List<Double?> = that.map { it gt this }
infix fun Double?.le(that: List<Double?>): List<Double?> = that.map { it ge this }
infix fun Double?.eq(that: List<Double?>): List<Double?> = that.map { it eq this }
infix fun Double?.ge(that: List<Double?>): List<Double?> = that.map { it le this }
infix fun Double?.gt(that: List<Double?>): List<Double?> = that.map { it lt this }

infix fun List<Double?>.and(that: List<Double?>): List<Double?> = mapIndexed { i, value -> value and that[i] }
infix fun List<Double?>.or(that: List<Double?>): List<Double?> = mapIndexed { i, value -> value or that[i] }
