package org.p8499.quant.tushare.dao

import org.p8499.quant.tushare.entity.Income
import java.util.*

interface IncomeDao {
    fun get(stockId: String, date: Date): Income?

    fun last(stockId: String): Income?

    fun findByStockId(stockId: String): List<Income>
}