package org.p8499.quant.tushare.dao

import org.p8499.quant.tushare.entity.Level1Candlestick
import java.util.*

interface Level1CandlestickDao {
    fun get(stockId: String, date: Date): Level1Candlestick?

    fun findByStockId(stockId: String): List<Level1Candlestick>

    fun previous(stockId: String, date: Date): Level1Candlestick?
}