package org.p8499.quant.tushare.service

import org.p8499.quant.tushare.entity.GroupStock
import org.p8499.quant.tushare.repository.GroupStockRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GroupStockService {
    @Autowired
    lateinit var groupStockRepository: GroupStockRepository

    fun saveAll(entityList: List<GroupStock>) = groupStockRepository.saveAll(entityList)
}