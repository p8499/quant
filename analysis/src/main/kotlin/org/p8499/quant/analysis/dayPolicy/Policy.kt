package org.p8499.quant.analysis.dayPolicy

import java.time.LocalDate

interface Policy<T : Status> {
    fun isTradingDate(date: LocalDate): Boolean
    fun proceed(stage: Stage<T>)
    fun hint(stage: Stage<T>): String
    val callingCommissions: MutableList<Commission>
    val openingCommissions: MutableList<Commission>
}