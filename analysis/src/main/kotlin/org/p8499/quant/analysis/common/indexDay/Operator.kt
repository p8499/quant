package org.p8499.quant.analysis.common.indexDay

import org.p8499.quant.analysis.common.*
import org.p8499.quant.analysis.entity.SecurityIndexDay

fun Double?.wrap(sid: SecurityIndexDay) = SecurityIndexDay(sid.region, sid.id, sid.type, sid.date, this)
fun List<Double?>.wrap(sidList: List<SecurityIndexDay>) = mapIndexed { i, value -> SecurityIndexDay(sidList[i].region, sidList[i].id, sidList[i].type, sidList[i].date, value) }

operator fun List<SecurityIndexDay>.plus(that: List<SecurityIndexDay>): List<SecurityIndexDay> = map(SecurityIndexDay::value).plus(that.map(SecurityIndexDay::value)).wrap(this)
operator fun List<SecurityIndexDay>.minus(that: List<SecurityIndexDay>): List<SecurityIndexDay> = map(SecurityIndexDay::value).minus(that.map(SecurityIndexDay::value)).wrap(this)
operator fun List<SecurityIndexDay>.times(that: List<SecurityIndexDay>): List<SecurityIndexDay> = map(SecurityIndexDay::value).times(that.map(SecurityIndexDay::value)).wrap(this)
operator fun List<SecurityIndexDay>.div(that: List<SecurityIndexDay>): List<SecurityIndexDay> = map(SecurityIndexDay::value).div(that.map(SecurityIndexDay::value)).wrap(this)

operator fun List<SecurityIndexDay>.plus(that: Double?): List<SecurityIndexDay> = map(SecurityIndexDay::value).plus(that).wrap(this)
operator fun List<SecurityIndexDay>.minus(that: Double?): List<SecurityIndexDay> = map(SecurityIndexDay::value).minus(that).wrap(this)
operator fun List<SecurityIndexDay>.times(that: Double?): List<SecurityIndexDay> = map(SecurityIndexDay::value).times(that).wrap(this)
operator fun List<SecurityIndexDay>.div(that: Double?): List<SecurityIndexDay> = map(SecurityIndexDay::value).div(that).wrap(this)

operator fun MutableList<SecurityIndexDay>.plusAssign(that: List<SecurityIndexDay>): Unit = forEachIndexed { i, sid -> this[i] = (sid.value + that[i].value).wrap(sid) }
operator fun MutableList<SecurityIndexDay>.minusAssign(that: List<SecurityIndexDay>): Unit = forEachIndexed { i, sid -> this[i] = (sid.value - that[i].value).wrap(sid) }
operator fun MutableList<SecurityIndexDay>.timesAssign(that: List<SecurityIndexDay>): Unit = forEachIndexed { i, sid -> this[i] = (sid.value * that[i].value).wrap(sid) }
operator fun MutableList<SecurityIndexDay>.divAssign(that: List<SecurityIndexDay>): Unit = forEachIndexed { i, sid -> this[i] = (sid.value / that[i].value).wrap(sid) }

operator fun MutableList<SecurityIndexDay>.plusAssign(that: Double?): Unit = forEachIndexed { i, sid -> this[i] = (sid.value + that).wrap(sid) }
operator fun MutableList<SecurityIndexDay>.minusAssign(that: Double?): Unit = forEachIndexed { i, sid -> this[i] = (sid.value - that).wrap(sid) }
operator fun MutableList<SecurityIndexDay>.timesAssign(that: Double?): Unit = forEachIndexed { i, sid -> this[i] = (sid.value * that).wrap(sid) }
operator fun MutableList<SecurityIndexDay>.divAssign(that: Double?): Unit = forEachIndexed { i, sid -> this[i] = (sid.value / that).wrap(sid) }

infix fun List<SecurityIndexDay>.between(range: ClosedRange<Double>): List<SecurityIndexDay> = map(SecurityIndexDay::value).between(range).wrap(this)

infix fun List<SecurityIndexDay>.lt(that: List<SecurityIndexDay>): List<SecurityIndexDay> = map(SecurityIndexDay::value).lt(that.map(SecurityIndexDay::value)).wrap(this)
infix fun List<SecurityIndexDay>.le(that: List<SecurityIndexDay>): List<SecurityIndexDay> = map(SecurityIndexDay::value).le(that.map(SecurityIndexDay::value)).wrap(this)
infix fun List<SecurityIndexDay>.eq(that: List<SecurityIndexDay>): List<SecurityIndexDay> = map(SecurityIndexDay::value).eq(that.map(SecurityIndexDay::value)).wrap(this)
infix fun List<SecurityIndexDay>.ge(that: List<SecurityIndexDay>): List<SecurityIndexDay> = map(SecurityIndexDay::value).ge(that.map(SecurityIndexDay::value)).wrap(this)
infix fun List<SecurityIndexDay>.gt(that: List<SecurityIndexDay>): List<SecurityIndexDay> = map(SecurityIndexDay::value).gt(that.map(SecurityIndexDay::value)).wrap(this)

infix fun List<SecurityIndexDay>.lt(that: Double?): List<SecurityIndexDay> = map(SecurityIndexDay::value).lt(that).wrap(this)
infix fun List<SecurityIndexDay>.le(that: Double?): List<SecurityIndexDay> = map(SecurityIndexDay::value).le(that).wrap(this)
infix fun List<SecurityIndexDay>.eq(that: Double?): List<SecurityIndexDay> = map(SecurityIndexDay::value).eq(that).wrap(this)
infix fun List<SecurityIndexDay>.ge(that: Double?): List<SecurityIndexDay> = map(SecurityIndexDay::value).ge(that).wrap(this)
infix fun List<SecurityIndexDay>.gt(that: Double?): List<SecurityIndexDay> = map(SecurityIndexDay::value).gt(that).wrap(this)

infix fun Double?.lt(that: List<SecurityIndexDay>): List<SecurityIndexDay> = lt(that.map(SecurityIndexDay::value)).wrap(that)
infix fun Double?.le(that: List<SecurityIndexDay>): List<SecurityIndexDay> = le(that.map(SecurityIndexDay::value)).wrap(that)
infix fun Double?.eq(that: List<SecurityIndexDay>): List<SecurityIndexDay> = eq(that.map(SecurityIndexDay::value)).wrap(that)
infix fun Double?.ge(that: List<SecurityIndexDay>): List<SecurityIndexDay> = ge(that.map(SecurityIndexDay::value)).wrap(that)
infix fun Double?.gt(that: List<SecurityIndexDay>): List<SecurityIndexDay> = gt(that.map(SecurityIndexDay::value)).wrap(that)

infix fun List<SecurityIndexDay>.and(that: List<SecurityIndexDay>): List<SecurityIndexDay> = map(SecurityIndexDay::value).and(that.map(SecurityIndexDay::value)).wrap(this)
infix fun List<SecurityIndexDay>.or(that: List<SecurityIndexDay>): List<SecurityIndexDay> = map(SecurityIndexDay::value).or(that.map(SecurityIndexDay::value)).wrap(this)

