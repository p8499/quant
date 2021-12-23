package org.p8499.quant.tushare.service

import org.p8499.quant.tushare.entity.Income
import org.p8499.quant.tushare.repository.IncomeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

@Service
class IncomeService {
    @Autowired
    protected lateinit var incomeRepository: IncomeRepository

    operator fun get(stockId: String, date: LocalDate) = incomeRepository.get(stockId, date)

    fun last(stockId: String) = incomeRepository.last(stockId)

    fun findByStockId(stockId: String) = incomeRepository.findByStockId(stockId)

    fun findByStockIdBetween(stockId: String, from: LocalDate, to: LocalDate) = incomeRepository.findByStockIdBetween(stockId, from, to)

    fun saveAll(entityIterable: Iterable<Income>): List<Income> = incomeRepository.saveAllAndFlush(entityIterable)
}