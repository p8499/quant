package org.p8499.quant.analysis.dayPolicy.cn.policy19

import org.p8499.quant.analysis.analyzer.RegionAnalyzer
import org.p8499.quant.analysis.analyzer.SecurityAnalyzer
import org.p8499.quant.analysis.common.indexDay.asDouble
import org.p8499.quant.analysis.common.indexDay.div
import org.p8499.quant.analysis.common.indexDay.ma
import org.p8499.quant.analysis.dayPolicy.cn.ps
import java.time.LocalDate

open class Policy19A(regionAnalyzer: RegionAnalyzer) : Policy19(regionAnalyzer) {
    override fun find(securities: List<SecurityAnalyzer>, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate): List<SecurityAnalyzer> {
        val filtered = securities.filter { isSafe(it, barDate, informDate, readyDate) }
        val ps = filtered.map { it.ps(barDate, informDate) }
        return filtered.indices.sortedWith(compareBy(
                //SOLUTION: 去除lod后表现稍好
//                { lod(ps[it], 5).asDouble() ?: Double.MAX_VALUE },
                { (ps[it] / ma(ps[it], 5)).asDouble() ?: Double.MAX_VALUE },
                { ps[it].asDouble() ?: Double.MAX_VALUE })).map(filtered::get)
    }

    //SOLUTION: 3表现最好
    override val slots = 3
}