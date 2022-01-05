package org.p8499.quant.analysis.service

import org.p8499.quant.analysis.entity.GroupMessageDay
import org.p8499.quant.analysis.repository.GroupMessageDayRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GroupMessageDayService {
    @Autowired
    protected lateinit var groupMessageDayRepository: GroupMessageDayRepository

    fun find(region: String, id: String) = groupMessageDayRepository.find(region, id)

    fun messages(region: String, id: String, limit: Int) = groupMessageDayRepository.messages(region, id, limit)

    fun save(entity: GroupMessageDay): GroupMessageDay = groupMessageDayRepository.saveAndFlush(entity)

    fun saveAll(entityIterable: Iterable<GroupMessageDay>): List<GroupMessageDay> = groupMessageDayRepository.saveAllAndFlush(entityIterable)

    fun delete(region: String, id: String) = groupMessageDayRepository.delete(region, id)
}