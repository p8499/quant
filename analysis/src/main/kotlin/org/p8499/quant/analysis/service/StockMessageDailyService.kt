package org.p8499.quant.analysis.service

import org.p8499.quant.analysis.entity.StockMessageDaily
import org.p8499.quant.analysis.repository.StockMessageDailyRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class StockMessageDailyService {
    @Autowired
    protected lateinit var stockMessageDailyRepository: StockMessageDailyRepository

    fun find(region: String, id: String) = stockMessageDailyRepository.find(region, id)

    fun messages(region: String, id: String, limit: Int) = stockMessageDailyRepository.messages(region, id, limit)

    fun save(entity: StockMessageDaily): StockMessageDaily = stockMessageDailyRepository.saveAndFlush(entity)

    fun saveAll(entityIterable: Iterable<StockMessageDaily>): List<StockMessageDaily> = stockMessageDailyRepository.saveAllAndFlush(entityIterable)

    fun delete(region: String, id: String) = stockMessageDailyRepository.delete(region, id)
}