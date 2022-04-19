package org.p8499.quant.analysis.analyzer

import org.p8499.quant.analysis.common.messageQuarter.flatten
import org.p8499.quant.analysis.common.messageQuarter.rename
import org.p8499.quant.analysis.common.messageQuarter.snapshot
import org.p8499.quant.analysis.common.snapshot
import org.p8499.quant.analysis.entity.SecurityMessageDay
import org.p8499.quant.analysis.entity.SecurityMessageQuarter
import org.p8499.quant.analysis.service.*
import org.slf4j.LoggerFactory
import java.time.LocalDate

class SecurityMessageQuarterAnalyzer(
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

    private val map: MutableMap<String, List<SecurityMessageQuarter>> = mutableMapOf()
    operator fun set(type: String, indices: List<SecurityMessageQuarter>) {
        map[type] = indices.also { it.rename(type) }
    }

    private operator fun get(type: String): List<SecurityMessageQuarter> {
        return map[type] ?: securityMessageQuarterService.find(region, id, type, from, to).also { map[type] = it }
    }

    operator fun get(type: String, asOf: LocalDate): List<SecurityMessageQuarter> {
        return get(type).snapshot(asOf)
    }

    operator fun get(type: String, asOf: LocalDate, limit: Int): List<SecurityMessageQuarter> {
        return get(type).snapshot(asOf, limit)
    }

    val flatten by lazy { Flatten() }

    inner class Flatten {
        operator fun get(type: String, asOf: LocalDate): List<SecurityMessageDay> {
            return get(type).snapshot(asOf).flatten(tradingDates.snapshot(asOf))
        }

        operator fun get(type: String, asOf: LocalDate, limit: Int): List<SecurityMessageDay> {
            return get(type).snapshot(asOf, limit).flatten(tradingDates.snapshot(asOf, limit))
        }
    }
}