package org.p8499.quant.analysis.dayPolicy.cn.policy16

import org.p8499.quant.analysis.analyzer.RegionAnalyzer
import org.p8499.quant.analysis.analyzer.SecurityAnalyzer
import org.p8499.quant.analysis.common.indexDay.*
import org.p8499.quant.analysis.common.indexQuarter.*
import org.p8499.quant.analysis.dayPolicy.cn.groupMacd
import org.p8499.quant.analysis.dayPolicy.cn.pe
import java.time.LocalDate

//不调整
open class Policy16H(regionAnalyzer: RegionAnalyzer) : Policy16(regionAnalyzer) {
    override val slots: Int = 4

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
        //因为pe会考虑message，所以消息会更提前
        //SOLUTION: 稳定性不如ps
        val pe = filtered.map { it.pe(barDate, informDate) }
        return filtered.indices.sortedWith(compareBy(
                //SOLUTION: 去除lod后表现稍好
//                { lod(ps[it], 5).asDouble() ?: Double.MAX_VALUE },
                { (pe[it] / ma(pe[it], 5)).asDouble() ?: Double.MAX_VALUE },
                { pe[it].asDouble() ?: Double.MAX_VALUE })).map(filtered::get)
    }
}
