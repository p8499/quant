package org.p8499.quant.analysis.dayPolicy.cn.policy16

import org.p8499.quant.analysis.analyzer.RegionAnalyzer
import org.p8499.quant.analysis.analyzer.SecurityAnalyzer

//不做调整
open class Policy16A(regionAnalyzer: RegionAnalyzer) : Policy16(regionAnalyzer) {
    override val slots = 4

    override fun isKeeping(security: SecurityAnalyzer): Boolean {
        return rawTargetSecurities.indexOf(security).let { it > -1 && it < rawTargetSecurities.size / 2 }
    }
}