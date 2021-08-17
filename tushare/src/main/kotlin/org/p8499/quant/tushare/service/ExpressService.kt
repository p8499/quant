package org.p8499.quant.tushare.service

import org.p8499.quant.tushare.entity.Express
import org.p8499.quant.tushare.repository.ExpressRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ExpressService {
    @Autowired
    protected lateinit var expressRepository: ExpressRepository

    fun findByStockId(stockId: String) = expressRepository.findByStockId(stockId)

    fun last(stockId: String) = expressRepository.last(stockId)

    fun saveAll(entityIterable: Iterable<Express>): List<Express> = expressRepository.saveAllAndFlush(entityIterable)
}