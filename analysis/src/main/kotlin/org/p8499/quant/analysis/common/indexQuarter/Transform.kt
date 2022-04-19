package org.p8499.quant.analysis.common.indexQuarter

import org.p8499.quant.analysis.common.let
import org.p8499.quant.analysis.common.period
import org.p8499.quant.analysis.common.quarter
import org.p8499.quant.analysis.common.year
import org.p8499.quant.analysis.entity.SecurityIndexDay
import org.p8499.quant.analysis.entity.SecurityIndexQuarter
import org.p8499.quant.analysis.entity.SecurityMessageQuarter
import java.time.LocalDate
import java.util.*

fun List<SecurityIndexQuarter>.asDouble() = lastOrNull()?.value

fun List<SecurityIndexQuarter>.asInt() = lastOrNull()?.value?.toInt()

fun List<SecurityIndexQuarter>.asBool() = asDouble() == 1.0

fun List<SecurityIndexQuarter>.rename(type: String?) = forEach { it.type = type }

fun List<SecurityIndexQuarter>.sort() =
        sortedWith(compareBy(SecurityIndexQuarter::publish, SecurityIndexQuarter::quarter))

fun List<SecurityIndexQuarter>.toMessages(valueTransform: (SecurityIndexQuarter) -> String?) = map {
    SecurityMessageQuarter(it.region, it.id, it.type, it.publish, it.quarter, valueTransform(it))
}

fun List<SecurityIndexQuarter>.snapshot(asOf: LocalDate): List<SecurityIndexQuarter> =
        subList(0, indexOfLast { sid -> sid.publish?.let { it <= asOf } == true } + 1)
                .groupBy(SecurityIndexQuarter::quarter).values.map(List<SecurityIndexQuarter>::last)
                .sortedBy(SecurityIndexQuarter::quarter)

fun List<SecurityIndexQuarter>.snapshot(asOf: LocalDate, limit: Int): List<SecurityIndexQuarter> =
        snapshot(asOf).takeLast(limit)

fun List<SecurityIndexQuarter>.slice(): List<SecurityIndexQuarter> {
    return groupBy { it.quarter?.year }.values.flatMap { accumList ->
        val sliceList = mutableListOf<SecurityIndexQuarter>()
        accumList.firstOrNull()?.also { model ->
            val v0 = accumList.lastOrNull { it.quarter?.period == 0 }?.let { siq ->
                siq.value.also {
                    sliceList.add(SecurityIndexQuarter(model.region, model.id, model.type, model.publish, quarter(model.quarter.year, 0), it))
                }
            }
            val v1 = accumList.lastOrNull { it.quarter?.period == 1 }?.let { siq ->
                siq.value.also {
                    sliceList.add(SecurityIndexQuarter(model.region, model.id, model.type, model.publish, quarter(model.quarter.year, 1), let(it, v0) { v1, v0 -> v1 - v0 }))
                }
            }
            val v2 = accumList.lastOrNull { it.quarter?.period == 2 }?.let { siq ->
                siq.value.also {
                    sliceList.add(SecurityIndexQuarter(model.region, model.id, model.type, model.publish, quarter(model.quarter.year, 2), let(it, v1) { v2, v1 -> v2 - v1 }))
                }
            }
            val v3 = accumList.lastOrNull { it.quarter?.period == 3 }?.let { siq ->
                siq.value.also {
                    sliceList.add(SecurityIndexQuarter(model.region, model.id, model.type, model.publish, quarter(model.quarter.year, 3), let(it, v2) { v3, v2 -> v3 - v2 }))
                }
            }
        }
        sliceList
    }
}

fun List<SecurityIndexQuarter>.accum(): List<SecurityIndexQuarter> {
    return groupBy { it.quarter?.year }.values.flatMap { sliceList ->
        val accumList = mutableListOf<SecurityIndexQuarter>()
        sliceList.firstOrNull()?.also { model ->
            val v0 = sliceList.lastOrNull { it.quarter?.period == 0 }?.let { siq ->
                siq.value.also {
                    accumList.add(SecurityIndexQuarter(model.region, model.id, model.type, model.publish, quarter(model.quarter.year, 0), it))
                }
            }
            val v1 = sliceList.lastOrNull { it.quarter?.period == 1 }?.let { siq ->
                siq.value.also {
                    accumList.add(SecurityIndexQuarter(model.region, model.id, model.type, model.publish, quarter(model.quarter.year, 1), let(v0, it) { v0, v1 -> v0 + v1 }))
                }
            }
            val v2 = sliceList.lastOrNull { it.quarter?.period == 2 }?.let { siq ->
                siq.value.also {
                    accumList.add(SecurityIndexQuarter(model.region, model.id, model.type, model.publish, quarter(model.quarter.year, 2), let(v0, v1, it) { v0, v1, v2 -> v0 + v1 + v2 }))
                }
            }
            val v3 = sliceList.lastOrNull { it.quarter?.period == 3 }?.let { siq ->
                siq.value.also {
                    accumList.add(SecurityIndexQuarter(model.region, model.id, model.type, model.publish, quarter(model.quarter.year, 3), let(v0, v1, v2, it) { v0, v1, v2, v3 -> v0 + v1 + v2 + v3 }))
                }
            }
        }
        accumList
    }
}

fun List<SecurityIndexQuarter>.previousYear() = map { current ->
    firstOrNull {
        let(it.quarter, current.quarter, it.publish, current.publish) { q, q0, p, p0 -> q == q0 - 4 && p <= p0 } == true
    } ?: SecurityIndexQuarter(current.region, current.id, current.type, null, current.quarter?.let { it - 4 }, null)
}


fun List<SecurityIndexQuarter>.flatten(dateList: List<LocalDate>, region: String, id: String): List<SecurityIndexDay> {
    val type = firstOrNull()?.type
    val map = TreeMap<LocalDate, SecurityIndexDay?>()
    for (date in dateList)
        map[date] = null
    for (siq in this)
        siq.publish?.let { map[it] = SecurityIndexDay(region, id, type, it, siq.value) }
    for (e in map)
        if (e.value === null)
            map[e.key] = map.lowerEntry(e.key)?.value?.let { SecurityIndexDay(it.region, it.id, it.type, e.key, it.value) }
    mapNotNull(SecurityIndexQuarter::publish).forEach {
        if (!dateList.contains(it)) map.remove(it)
    }
    return map.entries.toList().map { it.value ?: SecurityIndexDay(region, id, type, it.key, null) }
}