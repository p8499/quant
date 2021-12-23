package org.p8499.quant.tushare.service

import org.p8499.quant.tushare.entity.Express
import org.p8499.quant.tushare.repository.ExpressRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

@Service
class ExpressService {
    @Autowired
    protected lateinit var expressRepository: ExpressRepository

    fun last(stockId: String) = expressRepository.last(stockId)

    fun findByStockId(stockId: String) = expressRepository.findByStockId(stockId)

    fun findByStockIdBetween(stockId: String, from: LocalDate, to: LocalDate) = expressRepository.findByStockIdBetween(stockId, from, to)

    fun saveAll(entityIterable: Iterable<Express>): List<Express> = expressRepository.saveAllAndFlush(entityIterable)
}