package org.p8499.quant.tushare.service

import org.p8499.quant.tushare.entity.Cashflow
import org.p8499.quant.tushare.repository.CashflowRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

@Service
class CashflowService {
    @Autowired
    protected lateinit var cashflowRepository: CashflowRepository

    operator fun get(stockId: String, date: LocalDate) = cashflowRepository.get(stockId, date)

    fun last(stockId: String) = cashflowRepository.last(stockId)

    fun findByStockId(stockId: String) = cashflowRepository.findByStockId(stockId)

    fun findByStockIdBetween(stockId: String, from: LocalDate, to: LocalDate) = cashflowRepository.findByStockIdBetween(stockId, from, to)

    fun saveAll(entityIterable: Iterable<Cashflow>): List<Cashflow> = cashflowRepository.saveAllAndFlush(entityIterable)
}