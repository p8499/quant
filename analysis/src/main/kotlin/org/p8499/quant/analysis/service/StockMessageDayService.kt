package org.p8499.quant.analysis.service

import org.p8499.quant.analysis.entity.StockMessageDay
import org.p8499.quant.analysis.repository.StockMessageDayRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class StockMessageDayService {
    @Autowired
    protected lateinit var stockMessageDayRepository: StockMessageDayRepository

    fun find(region: String, id: String) = stockMessageDayRepository.find(region, id)

    fun messages(region: String, id: String, limit: Int) = stockMessageDayRepository.messages(region, id, limit)

    fun save(entity: StockMessageDay): StockMessageDay = stockMessageDayRepository.saveAndFlush(entity)

    fun saveAll(entityIterable: Iterable<StockMessageDay>): List<StockMessageDay> = stockMessageDayRepository.saveAllAndFlush(entityIterable)

    fun delete(region: String, id: String) = stockMessageDayRepository.delete(region, id)
}