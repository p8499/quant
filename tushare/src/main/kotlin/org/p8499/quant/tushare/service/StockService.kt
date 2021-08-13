package org.p8499.quant.tushare.service

import org.p8499.quant.tushare.entity.Stock
import org.p8499.quant.tushare.repository.StockRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class StockService {
    @Autowired
    protected lateinit var stockRepository: StockRepository

    fun findAll(): List<Stock> = stockRepository.findAll()

    operator fun get(stockId: String): Stock? = stockRepository.getById(stockId)

    fun findByStockIdList(stockIdList: List<String>) = stockRepository.findByStockIdList(stockIdList)

    fun findByExchangeId(exchangeId: String) = stockRepository.findByExchangeId(exchangeId)

    fun findByGroupId(groupId: String) = stockRepository.findByGroupId(groupId)

    fun saveAll(entityList: List<Stock>): List<Stock> = stockRepository.saveAllAndFlush(entityList)
}