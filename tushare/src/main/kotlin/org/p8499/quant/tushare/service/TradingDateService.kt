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

    fun unprocessedForLevel1(stockId: String) = tradingDateRepository.unprocessedForLevel1(stockId)

    fun unprocessedForLevel2(stockId: String) = tradingDateRepository.unprocessedForLevel2(stockId)

    fun saveAll(entityList: List<TradingDate>) = tradingDateRepository.saveAllAndFlush(entityList)
}