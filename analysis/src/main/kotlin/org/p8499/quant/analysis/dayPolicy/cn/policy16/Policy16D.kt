package org.p8499.quant.analysis.dayPolicy.cn.policy16

import org.p8499.quant.analysis.analyzer.RegionAnalyzer
import org.p8499.quant.analysis.analyzer.SecurityAnalyzer
import org.p8499.quant.analysis.common.indexDay.asDouble
import org.p8499.quant.analysis.common.indexDay.div
import org.p8499.quant.analysis.dayPolicy.cn.mcst
import java.time.LocalDate

open class Policy16D(regionAnalyzer: RegionAnalyzer) : Policy16(regionAnalyzer) {
    override val securities: List<SecurityAnalyzer> = super.securities.onEach {
        it.mcst()
    }

    override fun find(securities: List<SecurityAnalyzer>, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate): List<SecurityAnalyzer> {
        val filtered = securities.filter { isSafe(it, barDate, informDate, readyDate) }
        return filtered.sortedWith(compareBy { (it.indexDay["close", barDate] / it.indexDay["mcst", barDate]).asDouble() ?: Double.MAX_VALUE })
    }
}