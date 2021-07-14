package org.p8499.quant.tushare.dao

import org.p8499.quant.tushare.entity.TradingDate

interface TradingDateDao {
    fun last(exchangeId: String): TradingDate?
    fun unprocessedForLevel1(stockId: String): List<TradingDate>
    fun unprocessedForLevel2(stockId: String): List<TradingDate>
}