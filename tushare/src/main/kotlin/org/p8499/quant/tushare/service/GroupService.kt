package org.p8499.quant.tushare.service

import org.p8499.quant.tushare.entity.Group
import org.p8499.quant.tushare.repository.GroupRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GroupService {
    @Autowired
    protected lateinit var groupRepository: GroupRepository

    operator fun get(groupId: String): Group? = groupRepository.getById(groupId)

    fun findByType(type: Group.Type) = groupRepository.findByType(type)

    fun findAll(): List<Group> = groupRepository.findAll()

    fun saveAll(entityList: List<Group>): List<Group> = groupRepository.saveAllAndFlush(entityList)
}