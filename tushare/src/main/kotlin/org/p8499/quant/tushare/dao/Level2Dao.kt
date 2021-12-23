package org.p8499.quant.tushare.dao

import org.p8499.quant.tushare.entity.Level2
import java.time.LocalDate

interface Level2Dao {
    fun get(stockId: String, date: LocalDate): Level2?

    fun findByStockId(stockId: String): List<Level2>

    fun previous(stockId: String, date: LocalDate): Level2?

    fun findByStockIdBetween(stockId: String, from: LocalDate, to: LocalDate): List<Level2>
}