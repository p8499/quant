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

    fun save(entity: Level2) = level2Repository.saveAndFlush(entity)
}