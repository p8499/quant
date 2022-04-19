package org.p8499.quant.analysis.dayPolicy.common

import org.p8499.quant.analysis.analyzer.SecurityAnalyzer
import org.p8499.quant.analysis.common.indexDay.asDouble
import org.p8499.quant.analysis.dayPolicy.Position
import java.time.LocalDate

val Position.costValue get() = cost * volume

fun Position.price(asOf: LocalDate, priceAs: String) = security.indexDay[priceAs, asOf].asDouble()

fun Position.value(asOf: LocalDate, priceAs: String) = price(asOf, priceAs)?.let { it * volume }

fun Position.pl(asOf: LocalDate, priceAs: String) = value(asOf, priceAs) ?: 0.0 - costValue

fun Position.plPercent(asOf: LocalDate, priceAs: String) = pl(asOf, priceAs) / costValue

operator fun List<Position>.get(security: SecurityAnalyzer) = singleOrNull { it.security == security }

val List<Position>.costValue get() = sumOf(Position::costValue)

fun List<Position>.value(asOf: LocalDate, priceAs: String) = sumOf { it.value(asOf, priceAs) ?: 0.0 }

fun List<Position>.pl(asOf: LocalDate, priceAs: String) = sumOf { it.pl(asOf, priceAs) }

fun List<Position>.plPercent(asOf: LocalDate, priceAs: String) = pl(asOf, priceAs) / costValue