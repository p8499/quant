package org.p8499.quant.tushare.service

import org.p8499.quant.tushare.entity.TradingDate
import org.p8499.quant.tushare.repository.TradingDateRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class TradingDateService {
    @Autowired
    lateinit var tradingDateRepository: TradingDateRepository

    fun last(exchangeId: String) = tradingDateRepository.last(exchangeId)

    @Transactional
    fun saveAll(entityList: List<TradingDate>) = tradingDateRepository.saveAllAndFlush(entityList)
}