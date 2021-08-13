package org.p8499.quant.tushare.service

import org.p8499.quant.tushare.entity.BalanceSheet
import org.p8499.quant.tushare.repository.BalanceSheetRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class BalanceSheetService {
    @Autowired
    lateinit var balanceSheetRepository: BalanceSheetRepository

    operator fun get(stockId: String, date: Date) = balanceSheetRepository.get(stockId, date)

    fun findByStockId(stockId: String) = balanceSheetRepository.findByStockId(stockId)

    fun saveAll(entityList: List<BalanceSheet>): List<BalanceSheet> = balanceSheetRepository.saveAllAndFlush(entityList)
}