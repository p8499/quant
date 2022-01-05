package org.p8499.quant.analysis.service

import org.p8499.quant.analysis.entity.GroupIndexDay
import org.p8499.quant.analysis.repository.GroupIndexDayRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class GroupIndexDayService {
    @Autowired
    protected lateinit var groupIndexDayRepository: GroupIndexDayRepository

    fun find(region: String, id: String, kpi: String) = groupIndexDayRepository.find(region, id, kpi)

    fun size(region: String, id: String) = groupIndexDayRepository.size(region, id)

    fun dates(region: String, id: String, limit: Int) = groupIndexDayRepository.dates(region, id, limit)

    fun values(region: String, id: String, kpi: String, limit: Int) = groupIndexDayRepository.values(region, id, kpi, limit)

    fun tradingDates(region: String): List<LocalDate> = groupIndexDayRepository.tradingDates(region)

    fun save(entity: GroupIndexDay): GroupIndexDay = groupIndexDayRepository.saveAndFlush(entity)

    fun saveAll(entityIterable: Iterable<GroupIndexDay>): List<GroupIndexDay> = groupIndexDayRepository.saveAllAndFlush(entityIterable)

    fun delete(region: String, id: String) = groupIndexDayRepository.delete(region, id)
}