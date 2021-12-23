package org.p8499.quant.tushare.dao

import org.p8499.quant.tushare.entity.Level1Candlestick
import java.time.LocalDate

interface Level1CandlestickDao {
    fun get(stockId: String, date: LocalDate): Level1Candlestick?

    fun findByStockId(stockId: String): List<Level1Candlestick>

    fun previous(stockId: String, date: LocalDate): Level1Candlestick?

    fun findByStockIdBetween(stockId: String, from: LocalDate, to: LocalDate): List<Level1Candlestick>
}