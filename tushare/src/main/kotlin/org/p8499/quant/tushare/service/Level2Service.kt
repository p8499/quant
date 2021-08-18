package org.p8499.quant.tushare.service

import org.p8499.quant.tushare.entity.Level2
import org.p8499.quant.tushare.repository.Level2Repository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class Level2Service {
    @Autowired
    lateinit var level2Repository: Level2Repository

    operator fun get(stockId: String, date: Date) = level2Repository.get(stockId, date)

    fun findByStockId(stockId: String) = level2Repository.findByStockId(stockId)

    fun findByStockIdBetween(stockId: String, from: Date, to: Date) = level2Repository.findByStockIdBetween(stockId, from, to)

    fun saveAll(entityIterable: Iterable<Level2>): List<Level2> = level2Repository.saveAllAndFlush(entityIterable)
}