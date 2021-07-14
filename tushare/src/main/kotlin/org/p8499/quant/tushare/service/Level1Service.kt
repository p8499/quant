package org.p8499.quant.tushare.service

import org.p8499.quant.tushare.entity.Level1
import org.p8499.quant.tushare.repository.Level1Repository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class Level1Service {
    @Autowired
    lateinit var level1Repository: Level1Repository

    operator fun get(stockId: String, date: Date) = level1Repository.get(stockId, date)

    fun save(entity: Level1) = level1Repository.saveAndFlush(entity)
}