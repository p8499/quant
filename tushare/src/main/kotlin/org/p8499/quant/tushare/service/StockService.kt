package org.p8499.quant.tushare.service

import org.p8499.quant.tushare.entity.Stock
import org.p8499.quant.tushare.repository.StockRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class StockService {
    @Autowired
    lateinit var stockRepository: StockRepository

    @Transactional
    fun saveAll(entityList: List<Stock>) = stockRepository.saveAllAndFlush(entityList)
}