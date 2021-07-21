package org.p8499.quant.tushare.service

import org.p8499.quant.tushare.entity.Stock
import org.p8499.quant.tushare.repository.StockRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class StockService {
    @Autowired
    lateinit var stockRepository: StockRepository

    fun findAll() = stockRepository.findAll()

    fun findByStockIdList(stockIdList: List<String>) = stockRepository.findByStockIdList(stockIdList)

    fun findByExchangeId(exchangeId: String) = stockRepository.findByExchangeId(exchangeId)

    fun saveAll(entityList: List<Stock>) = stockRepository.saveAllAndFlush(entityList)
}