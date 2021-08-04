package org.p8499.quant.tushare.service

import org.p8499.quant.tushare.entity.Level1AdjFactor
import org.p8499.quant.tushare.entity.TradingDate
import org.p8499.quant.tushare.repository.Level1AdjFactorRepository
import org.p8499.quant.tushare.repository.TradingDateRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class Level1AdjFactorService {
    @Autowired
    protected lateinit var level1AdjFactorRepository: Level1AdjFactorRepository

    @Autowired
    protected lateinit var tradingDateRepository: TradingDateRepository

    operator fun get(stockId: String, date: Date) = level1AdjFactorRepository.get(stockId, date)

    fun findByStockId(stockId: String) = level1AdjFactorRepository.findByStockId(stockId)

    fun saveAll(entityList: List<Level1AdjFactor>) = level1AdjFactorRepository.saveAllAndFlush(entityList)

    @Transactional
    fun fillVacancies(stockId: String) {
        val dateList = tradingDateRepository.vacantForLevel1AdjFactor(stockId).mapNotNull(TradingDate::date)
        for (date in dateList)
            level1AdjFactorRepository.previous(stockId, date)?.copy(date = date)?.let(level1AdjFactorRepository::saveAndFlush)
    }
}