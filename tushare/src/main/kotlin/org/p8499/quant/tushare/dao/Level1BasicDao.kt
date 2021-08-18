package org.p8499.quant.tushare.dao

import org.p8499.quant.tushare.entity.Level1Basic
import java.util.*

interface Level1BasicDao {
    fun get(stockId: String, date: Date): Level1Basic?

    fun findByStockId(stockId: String): List<Level1Basic>

    fun previous(stockId: String, date: Date): Level1Basic?

    fun findByStockIdBetween(stockId: String, from: Date, to: Date): List<Level1Basic>
}