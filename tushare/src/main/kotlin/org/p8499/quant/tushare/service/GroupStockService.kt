package org.p8499.quant.tushare.service

import org.p8499.quant.tushare.entity.GroupStock
import org.p8499.quant.tushare.repository.GroupStockRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GroupStockService {
    @Autowired
    lateinit var groupStockRepository: GroupStockRepository

    fun findByGroupId(groupId: String) = groupStockRepository.findByGroupId(groupId)

    @Transactional
    fun deleteAndSaveAll(entityIterable: Iterable<GroupStock>): List<GroupStock> = groupStockRepository.run {
        deleteAll()
        saveAllAndFlush(entityIterable)
    }
}