package org.p8499.quant.tushare.dao

import org.p8499.quant.tushare.entity.Forecast
import java.time.LocalDate

interface ForecastDao {
    fun last(stockId: String): Forecast?

    fun findByStockId(stockId: String): List<Forecast>

    fun expires(stockId: String, year: Int, period: Int): LocalDate?
}