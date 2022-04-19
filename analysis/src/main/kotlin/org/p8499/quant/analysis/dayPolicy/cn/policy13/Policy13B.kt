package org.p8499.quant.analysis.dayPolicy.cn.policy13

import org.p8499.quant.analysis.analyzer.RegionAnalyzer
import org.p8499.quant.analysis.analyzer.SecurityAnalyzer
import org.p8499.quant.analysis.common.indexDay.asBool
import org.p8499.quant.analysis.common.indexDay.gt
import org.p8499.quant.analysis.common.indexQuarter.*
import org.p8499.quant.analysis.dayPolicy.cn.weekMacd
import java.time.LocalDate

open class Policy13B(regionAnalyzer: RegionAnalyzer) : Policy13(regionAnalyzer) {
    override fun isSafe(security: SecurityAnalyzer, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate): Boolean {
        return every(security.indexQuarter["profitForecast", informDate] gt 0.0, 12).asBool()
                && every(security.indexQuarter["profit", informDate].slice() gt 0.0, 12).asBool()
                //SOLUTION: 去掉every-4以后表现不佳
                && security.indexQuarter["profit", informDate].slice().let { it.previousYear() lt it }.asBool()
                //SOLUTION: 去掉every-4以后表现不佳
                && security.indexQuarter["profitForecast", informDate].let { ref(it, 1) lt it }.asBool()
                && security.indexQuarter["revenue", informDate].slice().let { it.previousYear() lt it }.asBool()
                && security.indexQuarter["revenueForecast", informDate].let { ref(it, 1) lt it }.asBool()
                && (security.weekMacd(barDate, readyDate) gt 0.0).asBool()
    }
}