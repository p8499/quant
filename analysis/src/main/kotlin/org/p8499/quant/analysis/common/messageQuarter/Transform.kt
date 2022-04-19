package org.p8499.quant.analysis.common.messageQuarter

import org.p8499.quant.analysis.entity.SecurityIndexQuarter
import org.p8499.quant.analysis.entity.SecurityMessageDay
import org.p8499.quant.analysis.entity.SecurityMessageQuarter
import java.time.LocalDate
import java.util.*

fun List<SecurityMessageQuarter>.asString() = lastOrNull()?.value

fun List<SecurityMessageQuarter>.rename(type: String?) = forEach { it.type = type }

fun List<SecurityMessageQuarter>.sort() = sortedWith(compareBy(SecurityMessageQuarter::publish, SecurityMessageQuarter::quarter))

fun List<SecurityMessageQuarter>.toIndices(valueTransform: (SecurityMessageQuarter) -> Double?) = map {
    SecurityIndexQuarter(it.region, it.id, it.type, it.publish, it.quarter, valueTransform(it))
}

fun List<SecurityMessageQuarter>.before(quarter: Int): List<SecurityMessageQuarter> = filter { it.quarter?.let { q -> q < quarter } == true }

fun List<SecurityMessageQuarter>.on(quarter: Int): List<SecurityMessageQuarter> = filter { it.quarter?.let { q -> q == quarter } == true }

fun List<SecurityMessageQuarter>.after(quarter: Int): List<SecurityMessageQuarter> = filter { it.quarter?.let { q -> q > quarter } == true }

fun List<SecurityMessageQuarter>.supply(others: List<SecurityMessageQuarter>): List<SecurityMessageQuarter> {
    val result = toMutableList()
    for (other in others)
        if (result.indexOfFirst { it.publish == other.publish && it.quarter == other.quarter } == -1)
            result += other
    return result.sort()
}

fun List<SecurityMessageQuarter>.expire(quarter: Int): List<SecurityMessageQuarter> {
    val result = toMutableList()
    result.removeAll { it.quarter?.let { a -> a <= quarter } == true }
    return result
}

fun List<SecurityMessageQuarter>.snapshot(asOf: LocalDate): List<SecurityMessageQuarter> =
        subList(0, indexOfLast { sid -> sid.publish?.let { it <= asOf } == true } + 1)
                .groupBy(SecurityMessageQuarter::quarter).values.map(List<SecurityMessageQuarter>::last)
                .sortedBy(SecurityMessageQuarter::quarter)

fun List<SecurityMessageQuarter>.snapshot(asOf: LocalDate, limit: Int): List<SecurityMessageQuarter> =
        snapshot(asOf).takeLast(limit)

fun List<SecurityMessageQuarter>.flatten(dateList: List<LocalDate>): List<SecurityMessageDay> {
    val map = TreeMap<LocalDate, SecurityMessageDay?>()
    for (date in dateList)
        map[date] = null
    for (siq in this)
        siq.publish?.let { map[it] = SecurityMessageDay(siq.region, siq.id, siq.type, it, siq.value) }
    for (e in map)
        if (e.value === null)
            map[e.key] = map.lowerEntry(e.key)?.value?.let { SecurityMessageDay(it.region, it.id, it.type, e.key, it.value) }
    mapNotNull(SecurityMessageQuarter::publish).forEach {
        if (!dateList.contains(it)) map.remove(it)
    }
    return if (isNotEmpty()) {
        val model = first()
        map.entries.toList().map {
            it.value
                    ?: SecurityMessageDay(model.region, model.id, model.type, it.key, null)
        }
    } else
        listOf()
}