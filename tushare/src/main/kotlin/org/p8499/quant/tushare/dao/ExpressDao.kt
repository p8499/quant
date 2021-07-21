package org.p8499.quant.tushare.dao

import org.p8499.quant.tushare.entity.Express

interface ExpressDao {
    fun findByStockId(stockId: String): List<Express>
}