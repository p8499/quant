package org.p8499.quant.analysis.common

import java.time.LocalDate

val Int?.year: Int? get() = this?.let { it / 4 }

val Int?.period: Int? get() = this?.let { it % 4 }

fun quarter(year: Int?, period: Int?): Int? = let(year, period) { a, b -> a * 4 + b }

fun quarter(date: LocalDate?): Int? = quarter(date?.year, date?.monthValue?.minus(1)?.div(4))

fun List<LocalDate>.snapshot(asOf: LocalDate): List<LocalDate> = subList(0, indexOfLast { it <= asOf } + 1)

fun List<LocalDate>.snapshot(asOf: LocalDate, limit: Int): List<LocalDate> = indexOfLast { it <= asOf }.let { subList(kotlin.math.max(0, it - limit + 1), it + 1) }

fun Double.floor(precision: Double): Double = kotlin.math.floor(this / precision).toInt() * precision

fun Double.ceil(precision: Double): Double = kotlin.math.ceil(this / precision).toInt() * precision

fun Double.round(precision: Double): Double = kotlin.math.round(this / precision).toInt() * precision
