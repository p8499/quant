package org.p8499.quant.tushare.dao

import org.p8499.quant.tushare.entity.Express

interface ExpressDao {
    fun last(stockId: String): Express?

    fun findByStockId(stockId: String): List<Express>
}