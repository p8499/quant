package org.p8499.quant.tushare.dao

import org.p8499.quant.tushare.entity.Income
import java.time.LocalDate

interface IncomeDao {
    fun get(stockId: String, date: LocalDate): Income?

    fun last(stockId: String): Income?

    fun findByStockId(stockId: String): List<Income>

    fun findByStockIdBetween(stockId: String, from: LocalDate, to: LocalDate): List<Income>
}