package org.p8499.quant.tushare.service

import org.p8499.quant.tushare.entity.TradingDate
import org.p8499.quant.tushare.repository.TradingDateRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TradingDateService {
    @Autowired
    lateinit var tradingDateRepository: TradingDateRepository

    fun last(exchangeId: String) = tradingDateRepository.last(exchangeId)

    fun unprocessedForLevel1Candlestick(stockId: String) = tradingDateRepository.unprocessedForLevel1Candlestick(stockId)

    fun firstUnprocessedForLevel1Candlestick(stockId: String) = tradingDateRepository.firstUnprocessedForLevel1Candlestick(stockId)

    fun unprocessedForLevel1Basic(stockId: String) = tradingDateRepository.unprocessedForLevel1Basic(stockId)

    fun firstUnprocessedForLevel1Basic(stockId: String) = tradingDateRepository.firstUnprocessedForLevel1Basic(stockId)

    fun unprocessedForLevel1AdjFactor(stockId: String) = tradingDateRepository.unprocessedForLevel1AdjFactor(stockId)

    fun firstUnprocessedForLevel1AdjFactor(stockId: String) = tradingDateRepository.firstUnprocessedForLevel1AdjFactor(stockId)

    fun unprocessedForLevel2(stockId: String) = tradingDateRepository.unprocessedForLevel2(stockId)

    fun firstUnprocessedForLevel2(stockId: String) = tradingDateRepository.firstUnprocessedForLevel2(stockId)

    fun saveAll(entityList: List<TradingDate>) = tradingDateRepository.saveAllAndFlush(entityList)
}