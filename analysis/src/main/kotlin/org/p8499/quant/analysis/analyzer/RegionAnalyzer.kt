package org.p8499.quant.analysis.analyzer

import org.p8499.quant.analysis.entity.Security
import org.p8499.quant.analysis.service.*
import java.time.LocalDate

class RegionAnalyzer(
        val region: String,
        from: LocalDate,
        to: LocalDate,
        protected val securityService: SecurityService,
        protected val securityIndexDayService: SecurityIndexDayService,
        protected val securityMessageDayService: SecurityMessageDayService,
        protected val securityIndexQuarterService: SecurityIndexQuarterService,
        protected val securityMessageQuarterService: SecurityMessageQuarterService) {

    val tradingDates by lazy { securityService.tradingDates(region).filter { it in from..to } }

    val from by lazy { tradingDates.firstOrNull() ?: LocalDate.EPOCH }

    val to by lazy { tradingDates.lastOrNull() ?: LocalDate.EPOCH }

    private val map: Map<String, SecurityAnalyzer> by lazy {
        securityService.find(region).mapNotNull(Security::id).associateWith { SecurityAnalyzer(region, it, this.from, this.to, tradingDates, securityService, securityIndexDayService, securityMessageDayService, securityIndexQuarterService, securityMessageQuarterService) }
    }

    fun securities(predicate: (SecurityAnalyzer) -> Boolean) = map.values.filter(predicate).toList()


    operator fun get(id: String): SecurityAnalyzer? {
        return map[id]
    }
}
