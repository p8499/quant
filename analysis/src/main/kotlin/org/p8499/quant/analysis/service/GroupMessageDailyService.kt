package org.p8499.quant.analysis.service

import org.p8499.quant.analysis.entity.GroupMessageDaily
import org.p8499.quant.analysis.repository.GroupMessageDailyRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GroupMessageDailyService {
    @Autowired
    protected lateinit var groupMessageDailyRepository: GroupMessageDailyRepository

    fun find(region: String, id: String) = groupMessageDailyRepository.find(region, id)

    fun messages(region: String, id: String, limit: Int) = groupMessageDailyRepository.messages(region, id, limit)

    fun save(entity: GroupMessageDaily): GroupMessageDaily = groupMessageDailyRepository.saveAndFlush(entity)

    fun saveAll(entityIterable: Iterable<GroupMessageDaily>): List<GroupMessageDaily> = groupMessageDailyRepository.saveAllAndFlush(entityIterable)

    fun delete(region: String, id: String) = groupMessageDailyRepository.delete(region, id)
}