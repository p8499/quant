package org.p8499.quant.analysis.dayPolicy.cn.policy13

import org.p8499.quant.analysis.analyzer.RegionAnalyzer
import org.p8499.quant.analysis.analyzer.SecurityAnalyzer
import org.p8499.quant.analysis.common.indexDay.asDouble
import org.p8499.quant.analysis.common.indexDay.div
import org.p8499.quant.analysis.common.indexDay.lod
import org.p8499.quant.analysis.common.indexDay.ma
import org.p8499.quant.analysis.dayPolicy.cn.ps
import java.time.LocalDate

open class Policy13F(regionAnalyzer: RegionAnalyzer) : Policy13(regionAnalyzer) {
//    override val securities: List<SecurityAnalyzer> = super.securities.onEach {
//        with(it.indexDay) {
//            this["ma5"] = ma(close, 5)
//        }
//    }

    override fun find(securities: List<SecurityAnalyzer>, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate): List<SecurityAnalyzer> {
        val filtered = securities.filter { isSafe(it, barDate, informDate, readyDate) }
        val ps = filtered.map { it.ps(barDate, informDate) }
        return filtered.indices.sortedWith(compareBy(
                { lod(filtered[it].indexDay["close", barDate], 5).asDouble() ?: Double.MAX_VALUE },
                { lod(ps[it], 5).asDouble() ?: Double.MAX_VALUE },
                { (ps[it] / ma(ps[it], 5)).asDouble() ?: Double.MAX_VALUE },
                { ps[it].asDouble() ?: Double.MAX_VALUE })).map(filtered::get)
    }
}