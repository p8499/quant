package org.p8499.quant.tushare.dao

import org.p8499.quant.tushare.entity.Level1Basic
import java.time.LocalDate

interface Level1BasicDao {
    fun get(stockId: String, date: LocalDate): Level1Basic?

    fun findByStockId(stockId: String): List<Level1Basic>

    fun previous(stockId: String, date: LocalDate): Level1Basic?

    fun findByStockIdBetween(stockId: String, from: LocalDate, to: LocalDate): List<Level1Basic>
}