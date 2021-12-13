package org.p8499.quant.analysis.common

fun Float.finiteOrNull(): Float? = if (isFinite()) this else null

fun Double.finiteOrNull(): Double? = if (isFinite()) this else null

fun Double.between(floor: Double, ceiling: Double): Boolean = this in floor..ceiling

operator fun Double?.plus(that: Double?): Double? = let(this, that) { a, b -> a + b }

operator fun Double?.minus(that: Double?): Double? = let(this, that) { a, b -> a - b }

operator fun Double?.times(that: Double?): Double? = let(this, that) { a, b -> a * b }

operator fun Double?.div(that: Double?): Double? = let(this, that) { a, b -> a / b }

operator fun List<Double?>.plus(that: List<Double?>): List<Double?> = mapIndexed { i, value -> let(value, that[i]) { a, b -> a + b } }

operator fun List<Double?>.minus(that: List<Double?>): List<Double?> = mapIndexed { i, value -> let(value, that[i]) { a, b -> a - b } }

operator fun List<Double?>.times(that: List<Double?>): List<Double?> = mapIndexed { i, value -> let(value, that[i]) { a, b -> a * b } }

operator fun List<Double?>.div(that: List<Double?>): List<Double?> = mapIndexed { i, value -> let(value, that[i]) { a, b -> a / b } }

operator fun List<Double?>.plusAssign(that: List<Double?>): Unit = run { forEachIndexed { i, value -> let(value, that[i]) { a, b -> a + b } } }

operator fun List<Double?>.minusAssign(that: List<Double?>): Unit = run { forEachIndexed { i, value -> let(value, that[i]) { a, b -> a - b } } }

operator fun List<Double?>.timesAssign(that: List<Double?>): Unit = run { forEachIndexed { i, value -> let(value, that[i]) { a, b -> a * b } } }

operator fun List<Double?>.divAssign(that: List<Double?>): Unit = run { forEachIndexed { i, value -> let(value, that[i]) { a, b -> a / b } } }

