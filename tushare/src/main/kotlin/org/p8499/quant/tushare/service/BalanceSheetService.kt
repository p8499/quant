package org.p8499.quant.tushare.service

import org.p8499.quant.tushare.entity.BalanceSheet
import org.p8499.quant.tushare.repository.BalanceSheetRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class BalanceSheetService {
    @Autowired
    lateinit var balanceSheetRepository: BalanceSheetRepository

    operator fun get(stockId: String, date: LocalDate) = balanceSheetRepository.get(stockId, date)

    fun last(stockId: String) = balanceSheetRepository.last(stockId)

    fun findByStockId(stockId: String) = balanceSheetRepository.findByStockId(stockId)

    fun findByStockIdBetween(stockId: String, from: LocalDate, to: LocalDate) = balanceSheetRepository.findByStockIdBetween(stockId, from, to)

    fun saveAll(entityIterable: Iterable<BalanceSheet>): List<BalanceSheet> = balanceSheetRepository.saveAllAndFlush(entityIterable)
}