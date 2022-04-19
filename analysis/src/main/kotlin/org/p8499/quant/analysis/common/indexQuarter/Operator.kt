package org.p8499.quant.analysis.common.indexQuarter

import org.p8499.quant.analysis.common.*
import org.p8499.quant.analysis.entity.SecurityIndexQuarter

fun Double?.wrap(sid: SecurityIndexQuarter) = SecurityIndexQuarter(sid.region, sid.id, sid.type, sid.publish, sid.quarter, this)
fun List<Double?>.wrap(sidList: List<SecurityIndexQuarter>) = mapIndexed { i, value -> SecurityIndexQuarter(sidList[i].region, sidList[i].id, sidList[i].type, sidList[i].publish, sidList[i].quarter, value) }

operator fun List<SecurityIndexQuarter>.plus(that: List<SecurityIndexQuarter>): List<SecurityIndexQuarter> = map(SecurityIndexQuarter::value).plus(that.map(SecurityIndexQuarter::value)).wrap(this)
operator fun List<SecurityIndexQuarter>.minus(that: List<SecurityIndexQuarter>): List<SecurityIndexQuarter> = map(SecurityIndexQuarter::value).minus(that.map(SecurityIndexQuarter::value)).wrap(this)
operator fun List<SecurityIndexQuarter>.times(that: List<SecurityIndexQuarter>): List<SecurityIndexQuarter> = map(SecurityIndexQuarter::value).times(that.map(SecurityIndexQuarter::value)).wrap(this)
operator fun List<SecurityIndexQuarter>.div(that: List<SecurityIndexQuarter>): List<SecurityIndexQuarter> = map(SecurityIndexQuarter::value).div(that.map(SecurityIndexQuarter::value)).wrap(this)

operator fun List<SecurityIndexQuarter>.plus(that: Double?): List<SecurityIndexQuarter> = map(SecurityIndexQuarter::value).plus(that).wrap(this)
operator fun List<SecurityIndexQuarter>.minus(that: Double?): List<SecurityIndexQuarter> = map(SecurityIndexQuarter::value).minus(that).wrap(this)
operator fun List<SecurityIndexQuarter>.times(that: Double?): List<SecurityIndexQuarter> = map(SecurityIndexQuarter::value).times(that).wrap(this)
operator fun List<SecurityIndexQuarter>.div(that: Double?): List<SecurityIndexQuarter> = map(SecurityIndexQuarter::value).div(that).wrap(this)

operator fun MutableList<SecurityIndexQuarter>.plusAssign(that: List<SecurityIndexQuarter>): Unit = forEachIndexed { i, sid -> this[i] = (sid.value + that[i].value).wrap(sid) }
operator fun MutableList<SecurityIndexQuarter>.minusAssign(that: List<SecurityIndexQuarter>): Unit = forEachIndexed { i, sid -> this[i] = (sid.value - that[i].value).wrap(sid) }
operator fun MutableList<SecurityIndexQuarter>.timesAssign(that: List<SecurityIndexQuarter>): Unit = forEachIndexed { i, sid -> this[i] = (sid.value * that[i].value).wrap(sid) }
operator fun MutableList<SecurityIndexQuarter>.divAssign(that: List<SecurityIndexQuarter>): Unit = forEachIndexed { i, sid -> this[i] = (sid.value / that[i].value).wrap(sid) }

operator fun MutableList<SecurityIndexQuarter>.plusAssign(that: Double?): Unit = forEachIndexed { i, sid -> this[i] = (sid.value + that).wrap(sid) }
operator fun MutableList<SecurityIndexQuarter>.minusAssign(that: Double?): Unit = forEachIndexed { i, sid -> this[i] = (sid.value - that).wrap(sid) }
operator fun MutableList<SecurityIndexQuarter>.timesAssign(that: Double?): Unit = forEachIndexed { i, sid -> this[i] = (sid.value * that).wrap(sid) }
operator fun MutableList<SecurityIndexQuarter>.divAssign(that: Double?): Unit = forEachIndexed { i, sid -> this[i] = (sid.value / that).wrap(sid) }

infix fun List<SecurityIndexQuarter>.between(range: ClosedRange<Double>): List<SecurityIndexQuarter> = map(SecurityIndexQuarter::value).between(range).wrap(this)

infix fun List<SecurityIndexQuarter>.lt(that: List<SecurityIndexQuarter>): List<SecurityIndexQuarter> = map(SecurityIndexQuarter::value).lt(that.map(SecurityIndexQuarter::value)).wrap(this)
infix fun List<SecurityIndexQuarter>.le(that: List<SecurityIndexQuarter>): List<SecurityIndexQuarter> = map(SecurityIndexQuarter::value).le(that.map(SecurityIndexQuarter::value)).wrap(this)
infix fun List<SecurityIndexQuarter>.eq(that: List<SecurityIndexQuarter>): List<SecurityIndexQuarter> = map(SecurityIndexQuarter::value).eq(that.map(SecurityIndexQuarter::value)).wrap(this)
infix fun List<SecurityIndexQuarter>.ge(that: List<SecurityIndexQuarter>): List<SecurityIndexQuarter> = map(SecurityIndexQuarter::value).ge(that.map(SecurityIndexQuarter::value)).wrap(this)
infix fun List<SecurityIndexQuarter>.gt(that: List<SecurityIndexQuarter>): List<SecurityIndexQuarter> = map(SecurityIndexQuarter::value).gt(that.map(SecurityIndexQuarter::value)).wrap(this)

infix fun List<SecurityIndexQuarter>.lt(that: Double?): List<SecurityIndexQuarter> = map(SecurityIndexQuarter::value).lt(that).wrap(this)
infix fun List<SecurityIndexQuarter>.le(that: Double?): List<SecurityIndexQuarter> = map(SecurityIndexQuarter::value).le(that).wrap(this)
infix fun List<SecurityIndexQuarter>.eq(that: Double?): List<SecurityIndexQuarter> = map(SecurityIndexQuarter::value).eq(that).wrap(this)
infix fun List<SecurityIndexQuarter>.ge(that: Double?): List<SecurityIndexQuarter> = map(SecurityIndexQuarter::value).ge(that).wrap(this)
infix fun List<SecurityIndexQuarter>.gt(that: Double?): List<SecurityIndexQuarter> = map(SecurityIndexQuarter::value).gt(that).wrap(this)

infix fun Double?.lt(that: List<SecurityIndexQuarter>): List<SecurityIndexQuarter> = lt(that.map(SecurityIndexQuarter::value)).wrap(that)
infix fun Double?.le(that: List<SecurityIndexQuarter>): List<SecurityIndexQuarter> = le(that.map(SecurityIndexQuarter::value)).wrap(that)
infix fun Double?.eq(that: List<SecurityIndexQuarter>): List<SecurityIndexQuarter> = eq(that.map(SecurityIndexQuarter::value)).wrap(that)
infix fun Double?.ge(that: List<SecurityIndexQuarter>): List<SecurityIndexQuarter> = ge(that.map(SecurityIndexQuarter::value)).wrap(that)
infix fun Double?.gt(that: List<SecurityIndexQuarter>): List<SecurityIndexQuarter> = gt(that.map(SecurityIndexQuarter::value)).wrap(that)

infix fun List<SecurityIndexQuarter>.and(that: List<SecurityIndexQuarter>): List<SecurityIndexQuarter> = map(SecurityIndexQuarter::value).and(that.map(SecurityIndexQuarter::value)).wrap(this)
infix fun List<SecurityIndexQuarter>.or(that: List<SecurityIndexQuarter>): List<SecurityIndexQuarter> = map(SecurityIndexQuarter::value).or(that.map(SecurityIndexQuarter::value)).wrap(this)
