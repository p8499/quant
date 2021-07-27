package org.p8499.quant.tushare.service

import org.p8499.quant.tushare.entity.Group
import org.p8499.quant.tushare.repository.GroupRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GroupService {
    @Autowired
    protected lateinit var groupRepository: GroupRepository

    fun findByType(type: Group.Type) = groupRepository.findByType(type)

    fun saveAll(entityList: List<Group>) = groupRepository.saveAllAndFlush(entityList)
}