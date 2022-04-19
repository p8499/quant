package org.p8499.quant.analysis.common.messageDay

import org.p8499.quant.analysis.entity.SecurityIndexDay
import org.p8499.quant.analysis.entity.SecurityMessageDay
import java.time.LocalDate
import kotlin.math.max

fun List<SecurityMessageDay>.asString() = lastOrNull()?.value

fun List<SecurityMessageDay>.rename(type: String?) = forEach { it.type = type }

fun List<SecurityMessageDay>.sort() = sortedBy(SecurityMessageDay::date)

fun List<SecurityMessageDay>.toIndices(valueTransform: (SecurityMessageDay) -> Double?) = map {
    SecurityIndexDay(it.region, it.id, it.type, it.date, valueTransform(it))
}

fun List<SecurityMessageDay>.snapshot(asOf: LocalDate): List<SecurityMessageDay> = subList(0, indexOfLast { sid -> sid.date?.let { it <= asOf } == true } + 1)

fun List<SecurityMessageDay>.snapshot(asOf: LocalDate, limit: Int): List<SecurityMessageDay> = indexOfLast { sid -> sid.date?.let { it <= asOf } == true }.let { subList(max(0, it - limit + 1), it + 1) }
