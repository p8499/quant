package org.p8499.quant.analysis.common

fun <A, B, R> let(a: A?, b: B?, block: (A, B) -> R): R? = if (a !== null && b !== null) block(a, b) else null

fun <A, B, C, R> let(a: A?, b: B?, c: C?, block: (A, B, C) -> R): R? = if (a !== null && b !== null && c !== null) block(a, b, c) else null

fun <A, B, C, D, R> let(a: A?, b: B?, c: C?, d: D?, block: (A, B, C, D) -> R): R? = if (a !== null && b !== null && c !== null && d !== null) block(a, b, c, d) else null

fun <A, B, C, D, E, R> let(a: A?, b: B?, c: C?, d: D?, e: E?, block: (A, B, C, D, E) -> R): R? = if (a !== null && b !== null && c !== null && d !== null && e !== null) block(a, b, c, d, e) else null
