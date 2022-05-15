package org.p8499.quant.analysis.common.indexDay

import org.p8499.quant.analysis.common.*
import org.p8499.quant.analysis.entity.SecurityIndexDay

fun ref(valueList: List<SecurityIndexDay>, n: Int): List<SecurityIndexDay> {
    val resultList = ref(valueList.map(SecurityIndexDay::value), n)
    return valueList.mapIndexed { i, sid -> resultList[i].wrap(sid) }
}

fun ref(valueList: List<SecurityIndexDay>, nList: List<SecurityIndexDay>): List<SecurityIndexDay> {
    val resultList = ref(valueList.map(SecurityIndexDay::value), valueList.map(SecurityIndexDay::value))
    return valueList.mapIndexed { i, sid -> resultList[i].wrap(sid) }
}

fun abs(valueList: List<SecurityIndexDay>): List<SecurityIndexDay> {
    val resultList = abs(valueList.map(SecurityIndexDay::value))
    return valueList.mapIndexed { i, sid -> resultList[i].wrap(sid) }
}

fun cross(valueList: List<SecurityIndexDay>, n: Double): List<SecurityIndexDay> {
    val resultList = cross(valueList.map(SecurityIndexDay::value), n)
    return valueList.mapIndexed { i, sid -> resultList[i].wrap(sid) }
}

fun cross(n: Double, valueList: List<SecurityIndexDay>): List<SecurityIndexDay> {
    val resultList = cross(n, valueList.map(SecurityIndexDay::value))
    return valueList.mapIndexed { i, sid -> resultList[i].wrap(sid) }
}

fun min(aList: List<SecurityIndexDay>, bList: List<SecurityIndexDay>): List<SecurityIndexDay> {
    val resultList = min(aList.map(SecurityIndexDay::value), bList.map(SecurityIndexDay::value))
    return aList.mapIndexed { i, sid -> resultList[i].wrap(sid) }
}

fun max(aList: List<SecurityIndexDay>, bList: List<SecurityIndexDay>): List<SecurityIndexDay> {
    val resultList = max(aList.map(SecurityIndexDay::value), bList.map(SecurityIndexDay::value))
    return aList.mapIndexed { i, sid -> resultList[i].wrap(sid) }
}

fun std(valueList: List<SecurityIndexDay>, n: Int): List<SecurityIndexDay> {
    val resultList = std(valueList.map(SecurityIndexDay::value), n)
    return valueList.mapIndexed { i, sid -> resultList[i].wrap(sid) }
}

fun ma(valueList: List<SecurityIndexDay>, n: Int): List<SecurityIndexDay> {
    val resultList = ma(valueList.map(SecurityIndexDay::value), n)
    return valueList.mapIndexed { i, sid -> resultList[i].wrap(sid) }
}

fun ma(valueList: List<SecurityIndexDay>, nList: List<SecurityIndexDay>): List<SecurityIndexDay> {
    val resultList = ma(valueList.map(SecurityIndexDay::value), nList.map(SecurityIndexDay::value))
    return valueList.mapIndexed { i, sid -> resultList[i].wrap(sid) }
}

fun dma(valueList: List<SecurityIndexDay>, weightList: List<SecurityIndexDay>): List<SecurityIndexDay> {
    val resultList = dma(valueList.map(SecurityIndexDay::value), weightList.map(SecurityIndexDay::value))
    return valueList.mapIndexed { i, sid -> resultList[i].wrap(sid) }
}

fun sma(valueList: List<SecurityIndexDay>, n: Int, m: Int): List<SecurityIndexDay> {
    val resultList = sma(valueList.map(SecurityIndexDay::value), n, m)
    return valueList.mapIndexed { i, sid -> resultList[i].wrap(sid) }
}

fun ema(valueList: List<SecurityIndexDay>, n: Int): List<SecurityIndexDay> {
    val resultList = ema(valueList.map(SecurityIndexDay::value), n)
    return valueList.mapIndexed { i, sid -> resultList[i].wrap(sid) }
}

fun mema(valueList: List<SecurityIndexDay>, n: Int): List<SecurityIndexDay> {
    val resultList = mema(valueList.map(SecurityIndexDay::value), n)
    return valueList.mapIndexed { i, sid -> resultList[i].wrap(sid) }
}

fun hod(valueList: List<SecurityIndexDay>, n: Int): List<SecurityIndexDay> {
    val resultList = hod(valueList.map(SecurityIndexDay::value), n)
    return valueList.mapIndexed { i, sid -> resultList[i].wrap(sid) }
}

fun hod(valueList: List<SecurityIndexDay>, nList: List<SecurityIndexDay>): List<SecurityIndexDay> {
    val resultList = hod(valueList.map(SecurityIndexDay::value), nList.map(SecurityIndexDay::value))
    return valueList.mapIndexed { i, sid -> resultList[i].wrap(sid) }
}

fun lod(valueList: List<SecurityIndexDay>, n: Int): List<SecurityIndexDay> {
    val resultList = lod(valueList.map(SecurityIndexDay::value), n)
    return valueList.mapIndexed { i, sid -> resultList[i].wrap(sid) }
}

fun lod(valueList: List<SecurityIndexDay>, nList: List<SecurityIndexDay>): List<SecurityIndexDay> {
    val resultList = lod(valueList.map(SecurityIndexDay::value), nList.map(SecurityIndexDay::value))
    return valueList.mapIndexed { i, sid -> resultList[i].wrap(sid) }
}

fun barscount(valueList: List<SecurityIndexDay>): List<SecurityIndexDay> {
    val resultList = barscount(valueList.map(SecurityIndexDay::value))
    return valueList.mapIndexed { i, sid -> resultList[i].wrap(sid) }
}

fun barslast(valueList: List<SecurityIndexDay>): List<SecurityIndexDay> {
    val resultList = barslast(valueList.map(SecurityIndexDay::value))
    return valueList.mapIndexed { i, sid -> resultList[i].wrap(sid) }
}

fun every(valueList: List<SecurityIndexDay>, n: Int): List<SecurityIndexDay> {
    val resultList = every(valueList.map(SecurityIndexDay::value), n)
    return valueList.mapIndexed { i, sid -> resultList[i].wrap(sid) }
}

fun hhv(valueList: List<SecurityIndexDay>, n: Int): List<SecurityIndexDay> {
    val resultList = hhv(valueList.map(SecurityIndexDay::value), n)
    return valueList.mapIndexed { i, sid -> resultList[i].wrap(sid) }
}

fun llv(valueList: List<SecurityIndexDay>, n: Int): List<SecurityIndexDay> {
    val resultList = llv(valueList.map(SecurityIndexDay::value), n)
    return valueList.mapIndexed { i, sid -> resultList[i].wrap(sid) }
}
