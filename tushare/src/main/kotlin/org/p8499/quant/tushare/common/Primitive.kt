package org.p8499.quant.tushare.common

fun Float.finiteOrNull(): Float? = if (isFinite()) this else null

fun Double.finiteOrNull(): Double? = if (isFinite()) this else null

fun Double.between(floor: Double, ceiling: Double): Boolean = this in floor..ceiling