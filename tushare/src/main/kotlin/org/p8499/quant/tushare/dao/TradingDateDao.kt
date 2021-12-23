package org.p8499.quant.tushare.dao

import org.p8499.quant.tushare.entity.TradingDate
import java.time.LocalDate

interface TradingDateDao {
    fun last(exchangeId: String): TradingDate?
    fun unprocessedForLevel1Candlestick(stockId: String): List<TradingDate>
    fun firstUnprocessedForLevel1Candlestick(stockId: String): TradingDate?
    fun vacantForLevel1Candlestick(stockId: String): List<TradingDate>
    fun unprocessedForLevel1Basic(stockId: String): List<TradingDate>
    fun firstUnprocessedForLevel1Basic(stockId: String): TradingDate?
    fun vacantForLevel1Basic(stockId: String): List<TradingDate>
    fun unprocessedForLevel1AdjFactor(stockId: String): List<TradingDate>
    fun firstUnprocessedForLevel1AdjFactor(stockId: String): TradingDate?
    fun vacantForLevel1AdjFactor(stockId: String): List<TradingDate>
    fun unprocessedForLevel2(stockId: String): List<TradingDate>
    fun firstUnprocessedForLevel2(stockId: String): TradingDate?
    fun vacantForLevel2(stockId: String): List<TradingDate>
    fun findByStockId(stockId: String): List<TradingDate>
    fun findByStockIdBetween(stockId: String, from: LocalDate, to: LocalDate): List<TradingDate>
    fun findByExchangeId(exchangeId: String): List<TradingDate>
}