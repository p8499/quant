package org.p8499.quant.analysis.analyzer

import org.p8499.quant.analysis.common.indexQuarter.flatten
import org.p8499.quant.analysis.common.indexQuarter.rename
import org.p8499.quant.analysis.common.indexQuarter.snapshot
import org.p8499.quant.analysis.common.snapshot
import org.p8499.quant.analysis.entity.SecurityIndexDay
import org.p8499.quant.analysis.entity.SecurityIndexQuarter
import org.p8499.quant.analysis.service.*
import org.slf4j.LoggerFactory
import java.time.LocalDate

class SecurityIndexQuarterAnalyzer(
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

    private val map: MutableMap<String, List<SecurityIndexQuarter>> = mutableMapOf()

    operator fun set(type: String, indices: List<SecurityIndexQuarter>) {
        map[type] = indices.also { it.rename(type) }
    }

    private operator fun get(type: String): List<SecurityIndexQuarter> {
        return map[type] ?: securityIndexQuarterService.find(region, id, type, from, to).also { map[type] = it }
    }

    operator fun get(type: String, asOf: LocalDate): List<SecurityIndexQuarter> {
        return get(type).snapshot(asOf)
    }

    operator fun get(type: String, asOf: LocalDate, limit: Int): List<SecurityIndexQuarter> {
        return get(type).snapshot(asOf, limit)
    }

    val flatten by lazy { Flatten() }

    inner class Flatten {
        operator fun get(type: String, dayAsOf: LocalDate, asOf: LocalDate): List<SecurityIndexDay> {
            return get(type).snapshot(asOf).flatten(tradingDates.snapshot(dayAsOf), region, id)
        }

        operator fun get(type: String, dayAsOf: LocalDate, asOf: LocalDate, limit: Int): List<SecurityIndexDay> {
            return get(type).snapshot(asOf, limit).flatten(tradingDates.snapshot(dayAsOf, limit), region, id)
        }
    }
}
