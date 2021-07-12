package org.p8499.quant.tushare.dao

import org.p8499.quant.tushare.entity.TradingDate

interface TradingDateDao {
    fun last(exchangeId: String): TradingDate?
}