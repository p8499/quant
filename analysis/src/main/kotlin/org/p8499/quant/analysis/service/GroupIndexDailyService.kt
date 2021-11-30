package org.p8499.quant.analysis.service

import org.p8499.quant.analysis.entity.GroupIndexDaily
import org.p8499.quant.analysis.repository.GroupIndexDailyRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GroupIndexDailyService {
    @Autowired
    protected lateinit var groupIndexDailyRepository: GroupIndexDailyRepository

    fun find(region: String, id: String, kpi: String) = groupIndexDailyRepository.find(region, id, kpi)

    fun save(entity: GroupIndexDaily): GroupIndexDaily = groupIndexDailyRepository.saveAndFlush(entity)

    fun saveAll(entityIterable: Iterable<GroupIndexDaily>): List<GroupIndexDaily> = groupIndexDailyRepository.saveAllAndFlush(entityIterable)

    fun delete(region: String, id: String) = groupIndexDailyRepository.delete(region, id)
}