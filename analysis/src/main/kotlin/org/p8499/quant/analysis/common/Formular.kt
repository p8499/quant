package org.p8499.quant.analysis.common

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

fun ref(valueList: List<Double?>, n: Int): List<Double?> {
    return valueList.indices.map { i ->
        if (i - n >= 0) valueList[i - n] else null
    }
}

fun abs(valueList: List<Double?>): List<Double?> {
    return valueList.map { it?.let(::abs) }
}

fun cross(valueList: List<Double?>, n: Double): List<Double?> {
    return valueList.mapIndexed { i, value ->
        val c = if (i > 0) let(valueList[i - 1], value) { a, b -> a < n && b > 0 } else null
        if (c == true) 1.0 else if (c == false) 0.0 else null
    }
}

fun cross(n: Double, valueList: List<Double?>): List<Double?> {
    return valueList.mapIndexed { i, value ->
        val c = if (i > 0) let(valueList[i - 1], value) { a, b -> a > n && b < 0 } else null
        if (c == true) 1.0 else if (c == false) 0.0 else null
    }
}

fun std(valueList: List<Double?>, n: Int): List<Double?> {
    return if (n > 0)
        valueList.indices.map { i ->
            val subList = valueList.subList(max(i - n + 1, 0), i + 1)
            val nonNullSubList = subList.mapNotNull { it }
            if (nonNullSubList.size == subList.size) {
                val average = nonNullSubList.sum() / nonNullSubList.size
                sqrt(nonNullSubList.sumOf { (it - average) * (it - average) } / nonNullSubList.size)
            } else
                null
        }
    else
        arrayOfNulls<Double>(valueList.size).toList()
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

fun ma(valueList: List<Double?>, nList: List<Double?>): List<Double?> {
    return if (valueList.size == nList.size)
        valueList.mapIndexed { i, value ->
            val n = nList[i]?.toInt()
            if (n !== null) {
                val elements = valueList.subList(max(i - n + 1, 0), i + 1)
                if (elements.all { it !== null }) elements.mapNotNull { it }.sum() / elements.size else null
            } else
                null
        }
    else
        arrayOfNulls<Double>(valueList.size).toList()
}

fun dma(valueList: List<Double?>, weightList: List<Double?>): List<Double?> {
    return if (valueList.size == weightList.size && weightList.filterNotNull().all { it in 0.0..1.0 }) {
        val resultList = mutableListOf<Double?>()
        valueList.forEachIndexed { i, value ->
            resultList.add(let(value, weightList[i]) { v, w -> v * w + (resultList.lastOrNull() ?: v) * (1 - w) })
        }
        resultList
    } else
        arrayOfNulls<Double>(valueList.size).toList()
}

fun sma(valueList: List<Double?>, n: Int, m: Int): List<Double?> {
    return if (n > m) {
        val resultList = mutableListOf<Double?>()
        valueList.forEach {
            resultList.add(it?.let { (it * m + (resultList.lastOrNull() ?: it) * (n - m)) / n })
        }
        resultList
    } else
        arrayOfNulls<Double>(valueList.size).toList()
}

fun ema(valueList: List<Double?>, n: Int): List<Double?> {
    return sma(valueList, n + 1, 2)
}

fun mema(valueList: List<Double?>, n: Int): List<Double?> {
    return sma(valueList, n, 1)
}

fun hod(valueList: List<Double?>, n: Int): List<Double?> {
    return if (n > 0)
        valueList.mapIndexed { i, value ->
            valueList.subList(max(i - n + 1, 0), i + 1).count { let(it, value) { a, b -> a > b } ?: false }.toDouble() + 1
        }
    else
        arrayOfNulls<Double>(valueList.size).toList()
}

fun hod(valueList: List<Double?>, nList: List<Double?>): List<Double?> {
    return if (valueList.size == nList.size)
        valueList.mapIndexed { i, value ->
            val n = nList[i]?.toInt()
            if (n !== null) valueList.subList(max(i - n + 1, 0), i + 1).count { let(it, value) { a, b -> a > b } ?: false }.toDouble() + 1 else null
        }
    else
        arrayOfNulls<Double>(valueList.size).toList()
}

fun lod(valueList: List<Double?>, n: Int): List<Double?> {
    return if (n > 0)
        valueList.mapIndexed { i, value ->
            valueList.subList(max(i - n + 1, 0), i + 1).count { let(it, value) { a, b -> a < b } ?: false }.toDouble() + 1
        }
    else
        arrayOfNulls<Double>(valueList.size).toList()
}

fun lod(valueList: List<Double?>, nList: List<Double?>): List<Double?> {
    return if (valueList.size == nList.size)
        valueList.mapIndexed { i, value ->
            val n = nList[i]?.toInt()
            if (n !== null) valueList.subList(max(i - n + 1, 0), i + 1).count { let(it, value) { a, b -> a < b } ?: false }.toDouble() + 1 else null
        }
    else
        arrayOfNulls<Double>(valueList.size).toList()
}

fun barscount(valueList: List<Double?>): List<Double?> {
    val first = valueList.indexOfLast { it == null } + 1
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
                if (i < 0) {
                    result = null
                    break
                }
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
