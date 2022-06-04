package org.p8499.quant.analysis.dayPolicy.cn.policy22

import org.p8499.quant.analysis.analyzer.RegionAnalyzer
import org.p8499.quant.analysis.analyzer.SecurityAnalyzer
import org.p8499.quant.analysis.common.indexDay.*
import org.p8499.quant.analysis.dayPolicy.cn.ps
import java.time.LocalDate

open class Policy22A(regionAnalyzer: RegionAnalyzer) : Policy22(regionAnalyzer) {
    override fun find(securities: List<SecurityAnalyzer>, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate): List<SecurityAnalyzer> {
        val filtered = securities.filter { isSafe(it, barDate, informDate, readyDate) }
        val ps = filtered.map { it.ps(barDate, informDate) }
        val dbars = filtered.map {
            max(barslast(it.indexDay["high", barDate].let { h -> ref(h, 1) lt h }),
                    barslast(it.indexDay["low", barDate].let { l -> ref(l, 1) lt l }))
        }
        return filtered.indices.sortedWith(compareBy(
                //SOLUTION: 去除lod后表现稍好
                { lod(ps[it], 5).asDouble() ?: Double.MAX_VALUE },
//                { (ps[it] / ma(ps[it], 5)).asDouble() ?: Double.MAX_VALUE },
                { dbars[it].asDouble()?.let { x -> -x } ?: Double.MAX_VALUE },
                { ps[it].asDouble() ?: Double.MAX_VALUE })).map(filtered::get)
    }
}