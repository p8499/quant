package org.p8499.quant.analysis.service

import org.p8499.quant.analysis.entity.GroupIndexDaily
import org.p8499.quant.analysis.repository.GroupIndexDailyRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GroupIndexDailyService {
    @Autowired
    protected lateinit var groupIndexDailyRepository: GroupIndexDailyRepository

    fun saveAll(entityIterable: Iterable<GroupIndexDaily>): List<GroupIndexDaily> = groupIndexDailyRepository.saveAllAndFlush(entityIterable)

    fun deleteById(id: String): Int = groupIndexDailyRepository.deleteById(id)
}