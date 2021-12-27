package org.p8499.quant.analysis.policy

import org.p8499.quant.analysis.common.let
import org.p8499.quant.analysis.common.minus
import org.p8499.quant.analysis.common.plus
import org.p8499.quant.analysis.common.times
import kotlin.math.max

fun ref(valueList: List<Double?>, n: Int): List<Double?> {
    return valueList.indices.map { i ->
        if (i - n >= 0) valueList[i - n] else null
    }
}

fun ma(valueList: List<Double?>, n: Int): List<Double?> {
    return if (n > 0)
        valueList.indices.map { i ->
            val elements = valueList.subList(max(i - n + 1, 0), i + 1)
            if (elements.all { it !== null }) elements.mapNotNull { it }.sum() / elements.size else null
        }
    else
        arrayOfNulls<Double>(valueList.size).toList()
}

fun dma(valueList: List<Double?>, weightList: List<Double?>): List<Double?> {
    return if (valueList.size == weightList.size && weightList.filterNotNull().all { it in 0.0..1.0 })
        valueList.mapIndexed { i, value ->
            val previousValue = if (i == 0) value else valueList[i - 1]
            value * weightList[i] + previousValue * (1.0 - weightList[i])
        }
    else
        arrayOfNulls<Double>(valueList.size).toList()
}

fun hod(valueList: List<Double?>, n: Int): List<Double?> {
    return if (n > 0)
        valueList.mapIndexed { i, value ->
            valueList.subList(max(i - n + 1, 0), i + 1).count { let(it, value) { a, b -> a > b } ?: false }.toDouble()
        }
    else
        arrayOfNulls<Double>(valueList.size).toList()
}

fun lod(valueList: List<Double?>, n: Int): List<Double?> {
    return if (n > 0)
        valueList.mapIndexed { i, value ->
            valueList.subList(max(i - n + 1, 0), i + 1).count { let(it, value) { a, b -> a < b } ?: false }.toDouble()
        }
    else
        arrayOfNulls<Double>(valueList.size).toList()
}

fun barscount(valueList: List<Double?>): List<Double?> {
    val first = valueList.indexOfFirst { it != null }.let { if (it > -1) it else valueList.size }
    return valueList.indices.map { i ->
        (i - first + 1).let { if (it < 0) 0 else it }.toDouble()
    }
}

fun barslast(valueList: List<Double?>): List<Double?> {
    val last: (Int) -> Int? = { iEnd ->
        var i = iEnd
        var result: Int? = null
        do {
            val value = valueList[i] ?: break
            if (value > 0.0) {
                result = iEnd - i
                break
            }
            i -= 1
        } while (i > -1)
        result
    }
    return valueList.indices.map { last(it) }.map { it?.toDouble() }
}

fun every(valueList: List<Double?>, n: Int): List<Double?> {
    return if (n > 1) {
        val check: (Int) -> Double? = { iEnd ->
            var i = iEnd
            var result: Double? = 1.0
            do {
                val value = valueList[i]
                if (value == null) {
                    result = null
                    break
                } else if (value <= 0.0) {
                    result = 0.0
                    break
                }
                i -= 1
            } while (i > iEnd - n)
            result
        }
        valueList.indices.map { i -> check(i) }
    } else
        arrayOfNulls<Double>(valueList.size).toList()
}

fun hhv(valueList: List<Double?>, n: Int): List<Double?> {
    return when {
        n > 0 -> valueList.indices.map { i ->
            valueList.subList(max(i - n + 1, 0), i + 1).maxByOrNull { it ?: Double.MAX_VALUE }
        }
        n == 0 -> valueList.indices.map { i ->
            valueList.subList(0, i + 1).maxByOrNull { it ?: Double.MAX_VALUE }
        }
        else -> arrayOfNulls<Double>(valueList.size).toList()
    }
}

fun llv(valueList: List<Double?>, n: Int): List<Double?> {
    return when {
        n > 0 -> valueList.indices.map { i ->
            valueList.subList(max(i - n + 1, 0), i + 1).minByOrNull { it ?: -Double.MAX_VALUE }
        }
        n == 0 -> valueList.indices.map { i ->
            valueList.subList(0, i + 1).minByOrNull { it ?: -Double.MAX_VALUE }
        }
        else -> arrayOfNulls<Double>(valueList.size).toList()
    }
}