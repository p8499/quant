package org.p8499.quant.analysis.service

import org.p8499.quant.analysis.entity.StockIndexQuarter
import org.p8499.quant.analysis.repository.StockIndexQuarterRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class StockIndexQuarterService {
    @Autowired
    protected lateinit var stockIndexQuarterRepository: StockIndexQuarterRepository

    fun dates(region: String, id: String, limit: Int) = stockIndexQuarterRepository.dates(region, id, limit)

    fun values(region: String, id: String, kpi: String, limit: Int) = stockIndexQuarterRepository.values(region, id, kpi, limit)

    fun publishes(region: String, id: String, kpi: String, limit: Int) = stockIndexQuarterRepository.publishes(region, id, kpi, limit)

    fun save(entity: StockIndexQuarter): StockIndexQuarter = stockIndexQuarterRepository.saveAndFlush(entity)

    fun saveAll(entityIterable: Iterable<StockIndexQuarter>): List<StockIndexQuarter> = stockIndexQuarterRepository.saveAllAndFlush(entityIterable)

    fun delete(region: String, id: String) = stockIndexQuarterRepository.delete(region, id)
}