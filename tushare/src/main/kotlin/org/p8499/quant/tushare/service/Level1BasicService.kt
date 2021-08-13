package org.p8499.quant.tushare.service

import org.p8499.quant.tushare.entity.Level1Basic
import org.p8499.quant.tushare.entity.TradingDate
import org.p8499.quant.tushare.repository.Level1BasicRepository
import org.p8499.quant.tushare.repository.TradingDateRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class Level1BasicService {
    @Autowired
    lateinit var level1BasicRepository: Level1BasicRepository

    @Autowired
    protected lateinit var tradingDateRepository: TradingDateRepository

    operator fun get(stockId: String, date: Date) = level1BasicRepository.get(stockId, date)

    fun findByStockId(stockId: String) = level1BasicRepository.findByStockId(stockId)

    fun saveAll(entityList: List<Level1Basic>): List<Level1Basic> = level1BasicRepository.saveAllAndFlush(entityList)

    @Transactional
    fun fillVacancies(stockId: String) {
        val dateList = tradingDateRepository.vacantForLevel1Basic(stockId).mapNotNull(TradingDate::date)
        for (date in dateList)
            level1BasicRepository.previous(stockId, date)?.copy(date = date)?.let(level1BasicRepository::saveAndFlush)
    }
}