package org.p8499.quant.analysis.policy

import org.p8499.quant.analysis.common.minus
import org.p8499.quant.analysis.common.plus
import org.p8499.quant.analysis.common.times

fun dma(valueList: List<Double?>, weightList: List<Double?>): List<Double?> {
    assert(valueList.size == weightList.size && weightList.filterNotNull().all { it in 0.0..1.0 })
    return valueList.mapIndexed { i, value ->
        val previousValue = if (i == 0) value else valueList[i - 1]
        value * weightList[i] + previousValue * (1.0 - weightList[i])
    }
}
