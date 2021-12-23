package org.p8499.quant.tushare.dao

import org.p8499.quant.tushare.entity.Level1AdjFactor
import java.time.LocalDate

interface Level1AdjFactorDao {
    fun get(stockId: String, date: LocalDate): Level1AdjFactor?

    fun findByStockId(stockId: String): List<Level1AdjFactor>

    fun previous(stockId: String, date: LocalDate): Level1AdjFactor?

    fun findByStockIdBetween(stockId: String, from: LocalDate, to: LocalDate): List<Level1AdjFactor>
}