package org.p8499.quant.analysis.common.indexQuarter

import org.p8499.quant.analysis.common.*
import org.p8499.quant.analysis.entity.SecurityIndexQuarter

fun ref(valueList: List<SecurityIndexQuarter>, n: Int): List<SecurityIndexQuarter> {
    val resultList = ref(valueList.map(SecurityIndexQuarter::value), n)
    return valueList.mapIndexed { i, sid -> resultList[i].wrap(sid) }
}
fun abs(valueList: List<SecurityIndexQuarter>): List<SecurityIndexQuarter> {
    val resultList = abs(valueList.map(SecurityIndexQuarter::value))
    return valueList.mapIndexed { i, sid -> resultList[i].wrap(sid) }
}
fun cross(valueList: List<SecurityIndexQuarter>, n: Double): List<SecurityIndexQuarter> {
    val resultList = cross(valueList.map(SecurityIndexQuarter::value), n)
    return valueList.mapIndexed { i, siq -> resultList[i].wrap(siq) }
}

fun cross(n: Double, valueList: List<SecurityIndexQuarter>): List<SecurityIndexQuarter> {
    val resultList = cross(n, valueList.map(SecurityIndexQuarter::value))
    return valueList.mapIndexed { i, siq -> resultList[i].wrap(siq) }
}

fun std(valueList: List<SecurityIndexQuarter>, n: Int): List<SecurityIndexQuarter> {
    val resultList = std(valueList.map(SecurityIndexQuarter::value), n)
    return valueList.mapIndexed { i, siq -> resultList[i].wrap(siq) }
}

fun ma(valueList: List<SecurityIndexQuarter>, n: Int): List<SecurityIndexQuarter> {
    val resultList = ma(valueList.map(SecurityIndexQuarter::value), n)
    return valueList.mapIndexed { i, siq -> resultList[i].wrap(siq) }
}

fun ma(valueList: List<SecurityIndexQuarter>, nList: List<SecurityIndexQuarter>): List<SecurityIndexQuarter> {
    val resultList = ma(valueList.map(SecurityIndexQuarter::value), nList.map(SecurityIndexQuarter::value))
    return valueList.mapIndexed { i, sid -> resultList[i].wrap(sid) }
}

fun dma(valueList: List<SecurityIndexQuarter>, weightList: List<SecurityIndexQuarter>): List<SecurityIndexQuarter> {
    val resultList = dma(valueList.map(SecurityIndexQuarter::value), weightList.map(SecurityIndexQuarter::value))
    return valueList.mapIndexed { i, siq -> resultList[i].wrap(siq) }
}

fun sma(valueList: List<SecurityIndexQuarter>, n: Int, m: Int): List<SecurityIndexQuarter> {
    val resultList = sma(valueList.map(SecurityIndexQuarter::value), n, m)
    return valueList.mapIndexed { i, siq -> resultList[i].wrap(siq) }
}

fun ema(valueList: List<SecurityIndexQuarter>, n: Int): List<SecurityIndexQuarter> {
    val resultList = ema(valueList.map(SecurityIndexQuarter::value), n)
    return valueList.mapIndexed { i, siq -> resultList[i].wrap(siq) }
}

fun mema(valueList: List<SecurityIndexQuarter>, n: Int): List<SecurityIndexQuarter> {
    val resultList = mema(valueList.map(SecurityIndexQuarter::value), n)
    return valueList.mapIndexed { i, siq -> resultList[i].wrap(siq) }
}

fun hod(valueList: List<SecurityIndexQuarter>, n: Int): List<SecurityIndexQuarter> {
    val resultList = hod(valueList.map(SecurityIndexQuarter::value), n)
    return valueList.mapIndexed { i, siq -> resultList[i].wrap(siq) }
}

fun hod(valueList: List<SecurityIndexQuarter>, nList: List<SecurityIndexQuarter>): List<SecurityIndexQuarter> {
    val resultList = hod(valueList.map(SecurityIndexQuarter::value), nList.map(SecurityIndexQuarter::value))
    return valueList.mapIndexed { i, sid -> resultList[i].wrap(sid) }
}

fun lod(valueList: List<SecurityIndexQuarter>, n: Int): List<SecurityIndexQuarter> {
    val resultList = lod(valueList.map(SecurityIndexQuarter::value), n)
    return valueList.mapIndexed { i, siq -> resultList[i].wrap(siq) }
}

fun lod(valueList: List<SecurityIndexQuarter>, nList: List<SecurityIndexQuarter>): List<SecurityIndexQuarter> {
    val resultList = lod(valueList.map(SecurityIndexQuarter::value), nList.map(SecurityIndexQuarter::value))
    return valueList.mapIndexed { i, sid -> resultList[i].wrap(sid) }
}

fun barscount(valueList: List<SecurityIndexQuarter>): List<SecurityIndexQuarter> {
    val resultList = barscount(valueList.map(SecurityIndexQuarter::value))
    return valueList.mapIndexed { i, siq -> resultList[i].wrap(siq) }
}

fun barslast(valueList: List<SecurityIndexQuarter>): List<SecurityIndexQuarter> {
    val resultList = barslast(valueList.map(SecurityIndexQuarter::value))
    return valueList.mapIndexed { i, siq -> resultList[i].wrap(siq) }
}

fun every(valueList: List<SecurityIndexQuarter>, n: Int): List<SecurityIndexQuarter> {
    val resultList = every(valueList.map(SecurityIndexQuarter::value), n)
    return valueList.mapIndexed { i, siq -> resultList[i].wrap(siq) }
}

fun hhv(valueList: List<SecurityIndexQuarter>, n: Int): List<SecurityIndexQuarter> {
    val resultList = hhv(valueList.map(SecurityIndexQuarter::value), n)
    return valueList.mapIndexed { i, sid -> resultList[i].wrap(sid) }
}

fun llv(valueList: List<SecurityIndexQuarter>, n: Int): List<SecurityIndexQuarter> {
    val resultList = llv(valueList.map(SecurityIndexQuarter::value), n)
    return valueList.mapIndexed { i, sid -> resultList[i].wrap(sid) }
}