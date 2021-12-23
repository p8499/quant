package org.p8499.quant.tushare.dao

import org.p8499.quant.tushare.entity.BalanceSheet
import java.time.LocalDate

interface BalanceSheetDao {
    fun get(stockId: String, date: LocalDate): BalanceSheet?

    fun last(stockId: String): BalanceSheet?

    fun findByStockId(stockId: String): List<BalanceSheet>

    fun findByStockIdBetween(stockId: String, from: LocalDate, to: LocalDate): List<BalanceSheet>
}