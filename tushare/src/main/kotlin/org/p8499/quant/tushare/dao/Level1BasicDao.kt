package org.p8499.quant.tushare.dao

import org.p8499.quant.tushare.entity.Level1Basic
import java.util.*

interface Level1BasicDao {
    fun get(stockId: String, date: Date): Level1Basic?
}