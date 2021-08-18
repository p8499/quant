package org.p8499.quant.tushare.dao

import org.p8499.quant.tushare.entity.BalanceSheet
import java.util.*

interface BalanceSheetDao {
    fun get(stockId: String, date: Date): BalanceSheet?

    fun last(stockId: String): BalanceSheet?

    fun findByStockId(stockId: String): List<BalanceSheet>

    fun findByStockIdBetween(stockId: String, from: Date, to: Date): List<BalanceSheet>
}