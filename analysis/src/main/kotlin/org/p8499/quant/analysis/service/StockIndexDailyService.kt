package org.p8499.quant.analysis.service

import org.p8499.quant.analysis.entity.StockIndexDaily
import org.p8499.quant.analysis.repository.StockIndexDailyRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class StockIndexDailyService {
    @Autowired
    protected lateinit var stockIndexDailyRepository: StockIndexDailyRepository

    fun find(region: String, id: String, kpi: String) = stockIndexDailyRepository.find(region, id, kpi)

    fun saveAll(entityIterable: Iterable<StockIndexDaily>): List<StockIndexDaily> = stockIndexDailyRepository.saveAllAndFlush(entityIterable)

    fun delete(region: String, id: String) = stockIndexDailyRepository.delete(region, id)
}