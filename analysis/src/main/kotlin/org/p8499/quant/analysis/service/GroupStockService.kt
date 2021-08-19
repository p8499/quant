package org.p8499.quant.analysis.service

import org.p8499.quant.analysis.entity.GroupStock
import org.p8499.quant.analysis.repository.GroupStockRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GroupStockService {
    @Autowired
    protected lateinit var groupStockRepository: GroupStockRepository

    fun findByGroup(region: String, groupId: String) = groupStockRepository.findByGroup(region, groupId)

    fun saveAll(entityIterable: Iterable<GroupStock>): List<GroupStock> = groupStockRepository.saveAllAndFlush(entityIterable)

    fun deleteByGroup(region: String, groupId: String): Int = groupStockRepository.deleteByGroup(region, groupId)
}