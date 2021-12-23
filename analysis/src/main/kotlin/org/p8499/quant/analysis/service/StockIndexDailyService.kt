package org.p8499.quant.analysis.service

import org.p8499.quant.analysis.entity.StockIndexDaily
import org.p8499.quant.analysis.repository.StockIndexDailyRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class StockIndexDailyService {
    @Autowired
    protected lateinit var stockIndexDailyRepository: StockIndexDailyRepository

    fun find(region: String, id: String, kpi: String) = stockIndexDailyRepository.find(region, id, kpi)

    fun size(region: String, id: String) = stockIndexDailyRepository.size(region, id)

    fun dates(region: String, id: String, limit: Int) = stockIndexDailyRepository.dates(region, id, limit)

    fun values(region: String, id: String, kpi: String, limit: Int) = stockIndexDailyRepository.values(region, id, kpi, limit)

    fun tradingDates(region: String): List<LocalDate> = stockIndexDailyRepository.tradingDates(region)

    fun save(entity: StockIndexDaily): StockIndexDaily = stockIndexDailyRepository.saveAndFlush(entity)

    fun saveAll(entityIterable: Iterable<StockIndexDaily>): List<StockIndexDaily> = stockIndexDailyRepository.saveAllAndFlush(entityIterable)

    fun delete(region: String, id: String) = stockIndexDailyRepository.delete(region, id)
}