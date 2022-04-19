package org.p8499.quant.analysis.analyzer

import org.p8499.quant.analysis.service.*
import org.slf4j.LoggerFactory
import java.time.LocalDate

class SecurityAnalyzer(
        val region: String,
        val id: String,
        from: LocalDate,
        to: LocalDate,
        tradingDates: List<LocalDate>,
        protected val securityService: SecurityService,
        protected val securityIndexDayService: SecurityIndexDayService,
        protected val securityMessageDayService: SecurityMessageDayService,
        protected val securityIndexQuarterService: SecurityIndexQuarterService,
        protected val securityMessageQuarterService: SecurityMessageQuarterService) {
    protected val logger by lazy { LoggerFactory.getLogger(javaClass) }

    val from by lazy { maxOf(from, securityService.firstDay(region, id) ?: LocalDate.EPOCH) }

    val to by lazy { minOf(to, securityService.lastDay(region, id) ?: LocalDate.EPOCH) }

    val tradingDates by lazy { tradingDates.filter { it in this.from..this.to } }

    val indexDay by lazy { SecurityIndexDayAnalyzer(region, id, this.from, this.to, this.tradingDates, securityService, securityIndexDayService, securityMessageDayService, securityIndexQuarterService, securityMessageQuarterService) }

    val indexQuarter by lazy { SecurityIndexQuarterAnalyzer(region, id, this.from, this.to, this.tradingDates, securityService, securityIndexDayService, securityMessageDayService, securityIndexQuarterService, securityMessageQuarterService) }

    val messageDay by lazy { SecurityMessageDayAnalyzer(region, id, this.from, this.to, this.tradingDates, securityService, securityIndexDayService, securityMessageDayService, securityIndexQuarterService, securityMessageQuarterService) }

    val messageQuarter by lazy { SecurityMessageQuarterAnalyzer(region, id, this.from, this.to, this.tradingDates, securityService, securityIndexDayService, securityMessageDayService, securityIndexQuarterService, securityMessageQuarterService) }

    override fun hashCode(): Int {
        return (region + id).hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is SecurityAnalyzer && region + id == other.region + other.id
    }
}