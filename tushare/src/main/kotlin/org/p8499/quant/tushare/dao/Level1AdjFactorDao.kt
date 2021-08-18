package org.p8499.quant.tushare.dao

import org.p8499.quant.tushare.entity.Level1AdjFactor
import java.util.*

interface Level1AdjFactorDao {
    fun get(stockId: String, date: Date): Level1AdjFactor?

    fun findByStockId(stockId: String): List<Level1AdjFactor>

    fun previous(stockId: String, date: Date): Level1AdjFactor?

    fun findByStockIdBetween(stockId: String, from: Date, to: Date): List<Level1AdjFactor>
}