package org.p8499.quant.tushare.dao

import org.p8499.quant.tushare.entity.Cashflow
import java.time.LocalDate

interface CashflowDao {
    fun get(stockId: String, date: LocalDate): Cashflow?

    fun last(stockId: String): Cashflow?

    fun findByStockId(stockId: String): List<Cashflow>

    fun findByStockIdBetween(stockId: String, from: LocalDate, to: LocalDate): List<Cashflow>
}