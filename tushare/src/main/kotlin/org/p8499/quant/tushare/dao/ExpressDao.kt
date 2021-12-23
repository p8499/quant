package org.p8499.quant.tushare.dao

import org.p8499.quant.tushare.entity.Express
import java.time.LocalDate

interface ExpressDao {
    fun last(stockId: String): Express?

    fun findByStockId(stockId: String): List<Express>

    fun findByStockIdBetween(stockId: String, from: LocalDate, to: LocalDate): List<Express>
}