package org.p8499.quant.tushare.service

import org.p8499.quant.tushare.entity.Level1Basic
import org.p8499.quant.tushare.repository.Level1BasicRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class Level1BasicService {
    @Autowired
    lateinit var level1BasicRepository: Level1BasicRepository

    operator fun get(stockId: String, date: Date) = level1BasicRepository.get(stockId, date)

    fun saveAll(entityList: List<Level1Basic>) = level1BasicRepository.saveAllAndFlush(entityList)
}