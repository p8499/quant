package org.p8499.quant.analysis.dayPolicy.cn

import java.time.LocalDate

fun convert(date: LocalDate, status: CNStatus): Pair<LocalDate, String> = when (status) {
    CNStatus.BEFORE, CNStatus.CALLING -> date.minusDays(1) to "close"
    CNStatus.OPENING, CNStatus.TRADING -> date to "open"
    CNStatus.CLOSING, CNStatus.AFTER -> date to "close"
}