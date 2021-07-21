package org.p8499.quant.tushare.service

import org.p8499.quant.tushare.entity.Level1AdjFactor
import org.p8499.quant.tushare.repository.Level1AdjFactorRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class Level1AdjFactorService {
    @Autowired
    lateinit var level1AdjFactorRepository: Level1AdjFactorRepository

    operator fun get(stockId: String, date: Date) = level1AdjFactorRepository.get(stockId, date)

    fun findByStockId(stockId: String) = level1AdjFactorRepository.findByStockId(stockId)

    fun saveAll(entityList: List<Level1AdjFactor>) = level1AdjFactorRepository.saveAllAndFlush(entityList)
}