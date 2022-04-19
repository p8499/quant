package org.p8499.quant.analysis.analyzer


import org.p8499.quant.analysis.common.indexDay.rename
import org.p8499.quant.analysis.common.indexDay.snapshot
import org.p8499.quant.analysis.entity.SecurityIndexDay
import org.p8499.quant.analysis.service.*
import org.slf4j.LoggerFactory
import java.time.LocalDate

class SecurityIndexDayAnalyzer(
        val region: String,
        val id: String,
        val from: LocalDate,
        val to: LocalDate,
        val tradingDates: List<LocalDate>,
        protected val securityService: SecurityService,
        protected val securityIndexDayService: SecurityIndexDayService,
        protected val securityMessageDayService: SecurityMessageDayService,
        protected val securityIndexQuarterService: SecurityIndexQuarterService,
        protected val securityMessageQuarterService: SecurityMessageQuarterService) {
    protected val logger by lazy { LoggerFactory.getLogger(javaClass) }

    private val map: MutableMap<String, List<SecurityIndexDay>> = mutableMapOf()

    operator fun set(type: String, indices: List<SecurityIndexDay>) {
        map[type] = indices.also { it.rename(type) }
    }

    operator fun get(type: String): List<SecurityIndexDay> {
        return map[type] ?: securityIndexDayService.find(region, id, type, from, to).also { map[type] = it }
    }

    operator fun get(type: String, asOf: LocalDate): List<SecurityIndexDay> {
        return get(type).snapshot(asOf)
    }

    operator fun get(type: String, asOf: LocalDate, limit: Int): List<SecurityIndexDay> {
        return get(type).snapshot(asOf, limit)
    }
}
