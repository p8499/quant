package org.p8499.quant.tushare.dao

import org.p8499.quant.tushare.entity.Level2
import java.util.*

interface Level2Dao {
    fun get(stockId: String, date: Date): Level2?
}