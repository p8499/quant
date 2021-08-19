package org.p8499.quant.analysis.service

import org.p8499.quant.analysis.entity.Group
import org.p8499.quant.analysis.repository.GroupRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GroupService {
    @Autowired
    protected lateinit var groupRepository: GroupRepository

    fun save(entity: Group): Group = groupRepository.save(entity)

    fun delete(region: String, id: String): Int = groupRepository.delete(region, id)
}