package org.p8499.quant.analysis.policy

import org.p8499.quant.analysis.common.div

data class AnalysisObject(
        val region: String,
        val id: String,
        val open: List<Double?>,
        val close: List<Double?>,
        val high: List<Double?>,
        val low: List<Double?>,
        val volume: List<Double?>,
        val amount: List<Double?>,
        val totalShare: List<Double?>,
        val flowShare: List<Double?>,
        val totalValue: List<Double?>,
        val flowValue: List<Double?>,
        val pb: List<Double?>,
        val pe: List<Double?>,
        val ps: List<Double?>,
        val pcf: List<Double?>) {
    val mcst: List<Double?> by lazy { dma(amount / volume, volume / flowShare) }
}



