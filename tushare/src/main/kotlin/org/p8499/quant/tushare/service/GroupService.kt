package org.p8499.quant.tushare.service

import org.p8499.quant.tushare.entity.Group
import org.p8499.quant.tushare.repository.GroupRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class GroupService {
    @Autowired
    lateinit var groupRepository: GroupRepository

    @Transactional
    fun saveAll(entityList: List<Group>) = groupRepository.saveAllAndFlush(entityList)
}