package org.p8499.quant.tushare.service

import org.p8499.quant.tushare.entity.Level1Candlestick
import org.p8499.quant.tushare.repository.Level1CandlestickRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class Level1CandlestickService {
    @Autowired
    protected lateinit var level1CandlestickRepository: Level1CandlestickRepository

    operator fun get(stockId: String, date: Date) = level1CandlestickRepository.get(stockId, date)

    fun findByStockId(stockId: String) = level1CandlestickRepository.findByStockId(stockId)

    fun saveAll(entityList: List<Level1Candlestick>) = level1CandlestickRepository.saveAllAndFlush(entityList)
}