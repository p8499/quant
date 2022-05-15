package org.p8499.quant.analysis.dayPolicy.cn.policy21

import org.p8499.quant.analysis.analyzer.RegionAnalyzer
import org.p8499.quant.analysis.analyzer.SecurityAnalyzer
import kotlin.math.ceil

open class Policy21A(regionAnalyzer: RegionAnalyzer) : Policy21(regionAnalyzer) {
    override val slots = 3
    override fun isKeeping(security: SecurityAnalyzer): Boolean {
        return rawTargetSecurities.indexOf(security).let { it > -1 && it <= slots * 2 && it < ceil(rawTargetSecurities.size / 2.0) }
    }
}