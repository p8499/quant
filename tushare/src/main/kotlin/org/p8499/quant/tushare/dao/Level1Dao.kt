package org.p8499.quant.tushare.dao

import org.p8499.quant.tushare.entity.Level1
import java.util.*

interface Level1Dao {
    fun get(stockId: String, date: Date): Level1?
}