package org.p8499.quant.tushare.dao

import org.p8499.quant.tushare.entity.Forecast

interface ForecastDao {
    fun last(stockId: String): Forecast?

    fun findByStockId(stockId: String): List<Forecast>
}