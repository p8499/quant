package org.p8499.quant.analysis.service

import org.p8499.quant.analysis.entity.StockIndexDay
import org.p8499.quant.analysis.repository.StockIndexDayRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class StockIndexDayService {
    @Autowired
    protected lateinit var stockIndexDayRepository: StockIndexDayRepository

    fun find(region: String, id: String, kpi: String) = stockIndexDayRepository.find(region, id, kpi)

    fun size(region: String, id: String) = stockIndexDayRepository.size(region, id)

    fun dates(region: String, id: String, limit: Int) = stockIndexDayRepository.dates(region, id, limit)

    fun values(region: String, id: String, kpi: String, limit: Int) = stockIndexDayRepository.values(region, id, kpi, limit)

    fun tradingDates(region: String): List<LocalDate> = stockIndexDayRepository.tradingDates(region)

    fun save(entity: StockIndexDay): StockIndexDay = stockIndexDayRepository.saveAndFlush(entity)

    fun saveAll(entityIterable: Iterable<StockIndexDay>): List<StockIndexDay> = stockIndexDayRepository.saveAllAndFlush(entityIterable)

    fun delete(region: String, id: String) = stockIndexDayRepository.delete(region, id)
}