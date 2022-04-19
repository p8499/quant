package org.p8499.quant.analysis.dayPolicy.cn.policy16

import org.p8499.quant.analysis.analyzer.RegionAnalyzer
import org.p8499.quant.analysis.analyzer.SecurityAnalyzer
import org.p8499.quant.analysis.common.indexDay.*
import org.p8499.quant.analysis.entity.SecurityIndexDay
import java.time.LocalDate

open class Policy16B(regionAnalyzer: RegionAnalyzer) : Policy16(regionAnalyzer) {
    protected fun SecurityAnalyzer.weekDif(barDate: LocalDate, asOf: LocalDate): List<SecurityIndexDay> {
        val weekClose = indexDay["close", barDate].lastByWeek(asOf)
        return ema(weekClose, 12) - ema(weekClose, 26)
    }

    override fun find(securities: List<SecurityAnalyzer>, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate): List<SecurityAnalyzer> {
        val filtered = securities.filter { isSafe(it, barDate, informDate, readyDate) }
        return filtered.sortedWith(compareBy { abs(it.weekDif(barDate, readyDate)).asDouble() ?: Double.MAX_VALUE })
    }
}