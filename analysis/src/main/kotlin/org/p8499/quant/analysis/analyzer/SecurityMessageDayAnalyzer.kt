package org.p8499.quant.analysis.analyzer

import org.p8499.quant.analysis.common.messageDay.rename
import org.p8499.quant.analysis.common.messageDay.snapshot
import org.p8499.quant.analysis.entity.SecurityMessageDay
import org.p8499.quant.analysis.service.*
import org.slf4j.LoggerFactory
import java.time.LocalDate

class SecurityMessageDayAnalyzer(
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

    private val map: MutableMap<String, List<SecurityMessageDay>> = mutableMapOf()

    operator fun set(type: String, indices: List<SecurityMessageDay>) {
        map[type] = indices.also { it.rename(type) }
    }

    operator fun get(type: String): List<SecurityMessageDay> {
        return map[type] ?: securityMessageDayService.find(region, id, type, from, to).also { map[type] = it }
    }

    operator fun get(type: String, asOf: LocalDate): List<SecurityMessageDay> {
        return get(type).snapshot(asOf)
    }

    operator fun get(type: String, asOf: LocalDate, limit: Int): List<SecurityMessageDay> {
        return get(type).snapshot(asOf, limit)
    }
}