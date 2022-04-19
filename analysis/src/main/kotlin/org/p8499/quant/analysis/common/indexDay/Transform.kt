package org.p8499.quant.analysis.common.indexDay

import org.p8499.quant.analysis.entity.SecurityIndexDay
import org.p8499.quant.analysis.entity.SecurityMessageDay
import java.time.LocalDate
import kotlin.math.max

fun List<SecurityIndexDay>.asDouble() = lastOrNull()?.value

fun List<SecurityIndexDay>.asInt() = lastOrNull()?.value?.toInt()

fun List<SecurityIndexDay>.asBool() = asDouble() == 1.0

fun List<SecurityIndexDay>.rename(type: String?) = forEach { it.type = type }

fun List<SecurityIndexDay>.sort() = sortedBy(SecurityIndexDay::date)

fun List<SecurityIndexDay>.toMessages(valueTransform: (SecurityIndexDay) -> String?) = map {
    SecurityMessageDay(it.region, it.id, it.type, it.date, valueTransform(it))
}

fun List<SecurityIndexDay>.snapshot(asOf: LocalDate): List<SecurityIndexDay> = subList(0, indexOfLast { sid -> sid.date?.let { it <= asOf } == true } + 1)

fun List<SecurityIndexDay>.snapshot(asOf: LocalDate, limit: Int): List<SecurityIndexDay> = indexOfLast { sid -> sid.date?.let { it <= asOf } == true }.let { subList(max(0, it - limit + 1), it + 1) }

fun List<SecurityIndexDay>.transformFirst() = first().let { SecurityIndexDay(it.region, it.id, it.type, it.date, it.value) }
fun List<SecurityIndexDay>.transformLast() = last().let { SecurityIndexDay(it.region, it.id, it.type, it.date, it.value) }
fun List<SecurityIndexDay>.transformHigh() = last().let { SecurityIndexDay(it.region, it.id, it.type, it.date, mapNotNull(SecurityIndexDay::value).maxOrNull()) }
fun List<SecurityIndexDay>.transformLow() = last().let { SecurityIndexDay(it.region, it.id, it.type, it.date, mapNotNull(SecurityIndexDay::value).minOrNull()) }
fun List<SecurityIndexDay>.transformSum() = last().let { SecurityIndexDay(it.region, it.id, it.type, it.date, mapNotNull(SecurityIndexDay::value).sum()) }

fun List<SecurityIndexDay>.byWeek(transform: (List<SecurityIndexDay>) -> SecurityIndexDay, asOf: LocalDate): List<SecurityIndexDay> {
    val sidList = groupBy { sid -> sid.date?.let { it.minusDays(it.dayOfWeek.value.toLong() - 1L) } }.values.map(transform)
    val nextStart = sidList.lastOrNull()?.date?.let { it.plusDays(8L - it.dayOfWeek.value.toLong()) }
    return nextStart?.let { if (asOf >= it) sidList else sidList.dropLast(1) } ?: listOf()
}

fun List<SecurityIndexDay>.byMonth(transform: (List<SecurityIndexDay>) -> SecurityIndexDay, asOf: LocalDate): List<SecurityIndexDay> {
    val sidList = groupBy { sid -> sid.date?.let { it.minusDays(it.dayOfMonth - 1L) } }.values.map(transform)
    val nextStart = sidList.lastOrNull()?.date?.let { it.minusDays(it.dayOfMonth - 1L).plusMonths(1) }
    return nextStart?.let { if (asOf >= it) sidList else sidList.dropLast(1) } ?: listOf()
}

fun List<SecurityIndexDay>.byQuarter(transform: (List<SecurityIndexDay>) -> SecurityIndexDay, asOf: LocalDate): List<SecurityIndexDay> {
    val sidList = groupBy { sid -> sid.date?.let { it.minusDays(it.dayOfMonth - 1L).minusMonths((it.monthValue - 1) % 3L) } }.values.map(transform)
    val nextStart = sidList.lastOrNull()?.date?.let { it.minusDays(it.dayOfMonth - 1L).plusMonths(3 - (it.monthValue - 1) % 3L) }
    return nextStart?.let { if (asOf >= it) sidList else sidList.dropLast(1) } ?: listOf()
}

fun List<SecurityIndexDay>.byYear(transform: (List<SecurityIndexDay>) -> SecurityIndexDay, asOf: LocalDate): List<SecurityIndexDay> {
    val sidList = groupBy { sid -> sid.date?.let { it.minusDays(it.dayOfYear - 1L) } }.values.map(transform)
    val nextStart = sidList.lastOrNull()?.date?.let { it.minusDays(it.dayOfYear - 1L).plusYears(1) }
    return nextStart?.let { if (asOf >= it) sidList else sidList.dropLast(1) } ?: listOf()
}

fun List<SecurityIndexDay>.by(groupSize: Int, transform: (List<SecurityIndexDay>) -> SecurityIndexDay): List<SecurityIndexDay> = indices.groupBy { i -> (size - i - 1) / groupSize }.values.map { it.map(this::get) }.map(transform)

fun List<SecurityIndexDay>.firstByWeek(asOf: LocalDate): List<SecurityIndexDay> = byWeek(List<SecurityIndexDay>::transformFirst, asOf)
fun List<SecurityIndexDay>.lastByWeek(asOf: LocalDate): List<SecurityIndexDay> = byWeek(List<SecurityIndexDay>::transformLast, asOf)
fun List<SecurityIndexDay>.highByWeek(asOf: LocalDate): List<SecurityIndexDay> = byWeek(List<SecurityIndexDay>::transformHigh, asOf)
fun List<SecurityIndexDay>.lowByWeek(asOf: LocalDate): List<SecurityIndexDay> = byWeek(List<SecurityIndexDay>::transformLow, asOf)
fun List<SecurityIndexDay>.sumByWeek(asOf: LocalDate): List<SecurityIndexDay> = byWeek(List<SecurityIndexDay>::transformSum, asOf)

fun List<SecurityIndexDay>.firstByMonth(asOf: LocalDate): List<SecurityIndexDay> = byMonth(List<SecurityIndexDay>::transformFirst, asOf)
fun List<SecurityIndexDay>.lastByMonth(asOf: LocalDate): List<SecurityIndexDay> = byMonth(List<SecurityIndexDay>::transformLast, asOf)
fun List<SecurityIndexDay>.highByMonth(asOf: LocalDate): List<SecurityIndexDay> = byMonth(List<SecurityIndexDay>::transformHigh, asOf)
fun List<SecurityIndexDay>.lowByMonth(asOf: LocalDate): List<SecurityIndexDay> = byMonth(List<SecurityIndexDay>::transformLow, asOf)
fun List<SecurityIndexDay>.sumByMonth(asOf: LocalDate): List<SecurityIndexDay> = byMonth(List<SecurityIndexDay>::transformSum, asOf)

fun List<SecurityIndexDay>.firstByQuarter(asOf: LocalDate): List<SecurityIndexDay> = byQuarter(List<SecurityIndexDay>::transformFirst, asOf)
fun List<SecurityIndexDay>.lastByQuarter(asOf: LocalDate): List<SecurityIndexDay> = byQuarter(List<SecurityIndexDay>::transformLast, asOf)
fun List<SecurityIndexDay>.highByQuarter(asOf: LocalDate): List<SecurityIndexDay> = byQuarter(List<SecurityIndexDay>::transformHigh, asOf)
fun List<SecurityIndexDay>.lowByQuarter(asOf: LocalDate): List<SecurityIndexDay> = byQuarter(List<SecurityIndexDay>::transformLow, asOf)
fun List<SecurityIndexDay>.sumByQuarter(asOf: LocalDate): List<SecurityIndexDay> = byQuarter(List<SecurityIndexDay>::transformSum, asOf)

fun List<SecurityIndexDay>.firstByYear(asOf: LocalDate): List<SecurityIndexDay> = byYear(List<SecurityIndexDay>::transformFirst, asOf)
fun List<SecurityIndexDay>.lastByYear(asOf: LocalDate): List<SecurityIndexDay> = byYear(List<SecurityIndexDay>::transformLast, asOf)
fun List<SecurityIndexDay>.highByYear(asOf: LocalDate): List<SecurityIndexDay> = byYear(List<SecurityIndexDay>::transformHigh, asOf)
fun List<SecurityIndexDay>.lowByYear(asOf: LocalDate): List<SecurityIndexDay> = byYear(List<SecurityIndexDay>::transformLow, asOf)
fun List<SecurityIndexDay>.sumByYear(asOf: LocalDate): List<SecurityIndexDay> = byYear(List<SecurityIndexDay>::transformSum, asOf)

fun List<SecurityIndexDay>.firstBy(groupSize: Int): List<SecurityIndexDay> = by(groupSize, List<SecurityIndexDay>::transformFirst)
fun List<SecurityIndexDay>.lastBy(groupSize: Int): List<SecurityIndexDay> = by(groupSize, List<SecurityIndexDay>::transformLast)
fun List<SecurityIndexDay>.highBy(groupSize: Int): List<SecurityIndexDay> = by(groupSize, List<SecurityIndexDay>::transformHigh)
fun List<SecurityIndexDay>.lowBy(groupSize: Int): List<SecurityIndexDay> = by(groupSize, List<SecurityIndexDay>::transformLow)
fun List<SecurityIndexDay>.sumBy(groupSize: Int): List<SecurityIndexDay> = by(groupSize, List<SecurityIndexDay>::transformSum)
