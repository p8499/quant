package org.p8499.quant.analysis.dayPolicy.cn.policy16

import org.p8499.quant.analysis.analyzer.RegionAnalyzer
import org.p8499.quant.analysis.analyzer.SecurityAnalyzer
import org.p8499.quant.analysis.common.indexDay.*
import org.p8499.quant.analysis.common.indexQuarter.*
import org.p8499.quant.analysis.dayPolicy.cn.groupMacd
import org.p8499.quant.analysis.dayPolicy.cn.j
import org.p8499.quant.analysis.dayPolicy.cn.kdj
import org.p8499.quant.analysis.dayPolicy.cn.ps
import java.time.LocalDate

//不调整
open class Policy16G(regionAnalyzer: RegionAnalyzer) : Policy16(regionAnalyzer) {
    override val slots: Int = 4

    override val securities: List<SecurityAnalyzer> = super.securities.onEach {
        it.kdj()
    }

    override fun isSafe(security: SecurityAnalyzer, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate): Boolean {
        return every(security.indexQuarter["profitForecast", informDate] gt 0.0, 12).asBool()
                && every(security.indexQuarter["profit", informDate].slice() gt 0.0, 12).asBool()
                && every(security.indexQuarter["profit", informDate].slice().let { it.previousYear() lt it }, 4).asBool()
                && every(security.indexQuarter["profitForecast", informDate].let { ref(it, 1) lt it }, 4).asBool()
                && security.indexQuarter["revenue", informDate].slice().let { it.previousYear() lt it }.asBool()
                && security.indexQuarter["revenueForecast", informDate].let { ref(it, 1) lt it }.asBool()
                //SOLUTION: byGroup比byWeek表现更好
                && (security.groupMacd(barDate, 5) gt 0.0).asBool()
    }

    override fun find(securities: List<SecurityAnalyzer>, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate): List<SecurityAnalyzer> {
        val filtered = securities.filter { isSafe(it, barDate, informDate, readyDate) }
        val ps = filtered.map { it.ps(barDate, informDate) }
        return filtered.indices.sortedWith(compareBy(
                //SOLUTION: 去除lod后表现稍好
//                { lod(ps[it], 5).asDouble() ?: Double.MAX_VALUE },
                { (ps[it] / ma(ps[it], 5)).asDouble() ?: Double.MAX_VALUE },
                { ps[it].asDouble() ?: Double.MAX_VALUE })).map(filtered::get)
    }

    override fun isKeeping(security: SecurityAnalyzer): Boolean {
        println("${security.id} ${security.indexDay.j.asDouble()}")
        return super.isKeeping(security)
    }
}
