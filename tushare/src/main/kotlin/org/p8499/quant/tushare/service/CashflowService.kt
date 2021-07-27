package org.p8499.quant.tushare.service

import org.p8499.quant.tushare.entity.Cashflow
import org.p8499.quant.tushare.repository.CashflowRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class CashflowService {
    @Autowired
    protected lateinit var cashflowRepository: CashflowRepository

    operator fun get(stockId: String, date: Date) = cashflowRepository.get(stockId, date)

    fun findByStockId(stockId: String) = cashflowRepository.findByStockId(stockId)

    fun saveAll(entityList: List<Cashflow>) = cashflowRepository.saveAllAndFlush(entityList)
}