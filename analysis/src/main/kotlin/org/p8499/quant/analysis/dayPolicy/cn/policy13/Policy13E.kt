package org.p8499.quant.analysis.dayPolicy.cn.policy13

import org.p8499.quant.analysis.analyzer.RegionAnalyzer
import org.p8499.quant.analysis.analyzer.SecurityAnalyzer
import org.p8499.quant.analysis.common.indexDay.*
import org.p8499.quant.analysis.common.indexQuarter.*
import org.p8499.quant.analysis.dayPolicy.cn.groupMacd
import org.p8499.quant.analysis.entity.SecurityIndexDay
import java.time.LocalDate

open class Policy13E(regionAnalyzer: RegionAnalyzer) : Policy13(regionAnalyzer) {
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
}