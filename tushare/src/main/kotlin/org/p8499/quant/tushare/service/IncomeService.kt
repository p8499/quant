package org.p8499.quant.tushare.service

import org.p8499.quant.tushare.entity.Income
import org.p8499.quant.tushare.repository.IncomeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class IncomeService {
    @Autowired
    protected lateinit var incomeRepository: IncomeRepository

    operator fun get(stockId: String, date: Date) = incomeRepository.get(stockId, date)

    fun last(stockId: String) = incomeRepository.last(stockId)

    fun findByStockId(stockId: String) = incomeRepository.findByStockId(stockId)

    fun saveAll(entityIterable: Iterable<Income>): List<Income> = incomeRepository.saveAllAndFlush(entityIterable)
}