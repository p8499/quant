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
    protected lateinit var stockIndexDailyService: StockIndexDailyService

    @Autowired
    protected lateinit var stockMessageDailyService: StockMessageDailyService

    fun dates(region: String, id: String, limit: Int) = stockIndexDailyService.dates(region, id, limit)

    fun size(region: String, id: String) = stockIndexDailyService.size(region, id)

    fun values(region: String, id: String, kpi: String, limit: Int) = stockIndexDailyService.values(region, id, kpi, limit)

    fun messages(region: String, id: String, limit: Int) = stockMessageDailyService.messages(region, id, limit)

    fun tradingDates(region: String): List<LocalDate> = stockIndexDailyService.tradingDates(region)

    fun securitiesByRegion(region: String, limit: Int) = stockService.find(region).mapNotNull { let(it.region, it.id) { region, id -> security(region, id, limit) } }

    fun security(region: String, id: String, limit: Int) = Security(region, id, limit, this::size, this::dates, this::values, this::messages)
}