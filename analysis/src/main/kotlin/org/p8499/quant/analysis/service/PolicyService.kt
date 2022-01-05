package org.p8499.quant.analysis.service

import org.p8499.quant.analysis.common.let
import org.p8499.quant.analysis.policy.Security
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class PolicyService {
    @Autowired
    protected lateinit var stockService: StockService

    @Autowired
    protected lateinit var stockIndexDayService: StockIndexDayService

    @Autowired
    protected lateinit var stockMessageDayService: StockMessageDayService

    @Autowired
    protected lateinit var stockIndexQuarterService: StockIndexQuarterService

    fun size(region: String, id: String) = stockIndexDayService.size(region, id)

    fun dates(region: String, id: String, limit: Int) = stockIndexDayService.dates(region, id, limit)

    fun values(region: String, id: String, kpi: String, limit: Int) = stockIndexDayService.values(region, id, kpi, limit)

    fun messages(region: String, id: String, limit: Int) = stockMessageDayService.messages(region, id, limit)

    fun quarterDays(region: String, id: String, limit: Int) = stockIndexQuarterService.dates(region, id, limit)

    fun quarterValues(region: String, id: String, kpi: String, limit: Int) = stockIndexQuarterService.values(region, id, kpi, limit)

    fun quarterPublishes(region: String, id: String, kpi: String, limit: Int) = stockIndexQuarterService.publishes(region, id, kpi, limit)

    fun tradingDates(region: String): List<LocalDate> = stockIndexDayService.tradingDates(region)

    fun securitiesByRegion(region: String, limit: Int) = stockService.find(region).mapNotNull { let(it.region, it.id) { region, id -> security(region, id, limit) } }

    fun security(region: String, id: String, limit: Int) = Security(region, id, limit, ::dates, ::values, ::messages, ::quarterDays, ::quarterValues, ::quarterPublishes)
}