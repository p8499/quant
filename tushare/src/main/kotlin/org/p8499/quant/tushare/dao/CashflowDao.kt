package org.p8499.quant.tushare.dao

import org.p8499.quant.tushare.entity.Cashflow
import java.util.*

interface CashflowDao {
    fun get(stockId: String, date: Date): Cashflow?

    fun findByStockId(stockId: String): List<Cashflow>
}