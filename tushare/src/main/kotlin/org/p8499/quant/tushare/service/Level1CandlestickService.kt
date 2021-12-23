package org.p8499.quant.tushare.service

import org.p8499.quant.tushare.entity.Level1Candlestick
import org.p8499.quant.tushare.entity.TradingDate
import org.p8499.quant.tushare.repository.Level1CandlestickRepository
import org.p8499.quant.tushare.repository.TradingDateRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.*

@Service
class Level1CandlestickService {
    @Autowired
    protected lateinit var level1CandlestickRepository: Level1CandlestickRepository

    @Autowired
    protected lateinit var tradingDateRepository: TradingDateRepository

    operator fun get(stockId: String, date: LocalDate) = level1CandlestickRepository.get(stockId, date)

    fun findByStockId(stockId: String) = level1CandlestickRepository.findByStockId(stockId)

    fun findByStockIdBetween(stockId: String, from: LocalDate, to: LocalDate) = level1CandlestickRepository.findByStockIdBetween(stockId, from, to)

    fun previous(stockId: String, date: LocalDate) = level1CandlestickRepository.previous(stockId, date)

    fun saveAll(entityIterable: Iterable<Level1Candlestick>): List<Level1Candlestick> = level1CandlestickRepository.saveAllAndFlush(entityIterable)

    @Transactional
    fun fillVacancies(stockId: String) {
        val dateList = tradingDateRepository.vacantForLevel1Candlestick(stockId).mapNotNull(TradingDate::date)
        for (date in dateList)
            level1CandlestickRepository.previous(stockId, date)?.let { Level1Candlestick(it.stockId, date, it.close, it.close, it.close, it.close, 0.0, 0.0) }?.let(level1CandlestickRepository::saveAndFlush)
    }
}