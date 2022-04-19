package org.p8499.quant.analysis.dayPolicy.cn.policy16

import org.p8499.quant.analysis.analyzer.RegionAnalyzer
import org.p8499.quant.analysis.analyzer.SecurityAnalyzer
import org.p8499.quant.analysis.common.indexDay.asBool
import org.p8499.quant.analysis.common.indexDay.gt
import org.p8499.quant.analysis.dayPolicy.cn.mcst
import java.time.LocalDate

open class Policy16E(regionAnalyzer: RegionAnalyzer) : Policy16(regionAnalyzer) {
    override val securities: List<SecurityAnalyzer> = super.securities.onEach {
        it.mcst()
    }

    override fun isSafe(security: SecurityAnalyzer, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate): Boolean {
        return super.isSafe(security, barDate, informDate, readyDate) && (security.indexDay["close", barDate] gt security.indexDay["mcst", barDate]).asBool()
    }
}