package org.p8499.quant.analysis.service

import org.p8499.quant.analysis.entity.SecurityIndexDay
import org.p8499.quant.analysis.repository.SecurityIndexDayRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class SecurityIndexDayService {
    @Autowired
    protected lateinit var securityIndexDayRepository: SecurityIndexDayRepository

    fun find(region: String, id: String, type: String): List<SecurityIndexDay> = securityIndexDayRepository.find(region, id, type)

    fun find(region: String, id: String, type: String, from: LocalDate, to: LocalDate) = securityIndexDayRepository.find(region, id, type, from, to)

    fun size(region: String, id: String, type: String): Int = securityIndexDayRepository.size(region, id, type)

    fun firstOrNull(region: String, id: String, type: String): SecurityIndexDay? = securityIndexDayRepository.firstOrNull(region, id, type)

    fun lastOrNull(region: String, id: String, type: String): SecurityIndexDay? = securityIndexDayRepository.lastOrNull(region, id, type)

    fun dates(region: String, id: String, type: String, limit: Int): List<LocalDate> = securityIndexDayRepository.dates(region, id, type, limit)

    fun values(region: String, id: String, type: String, limit: Int): List<Double?> = securityIndexDayRepository.values(region, id, type, limit)

    @Transactional
    fun saveAll(region: String, id: String, type: String, entityList: List<SecurityIndexDay>): List<SecurityIndexDay> {
        var append = false
        val size = size(region, id, type)
        val first = firstOrNull(region, id, type)
        val last = lastOrNull(region, id, type)
        if (size <= entityList.size) {
            val correspondingFirst = entityList.firstOrNull()
            val correspondingLast = size.takeIf { it > 0 }?.let { entityList[it - 1] }
            append = first == correspondingFirst && last == correspondingLast
        }
        return if (append)
            securityIndexDayRepository.saveAllAndFlush(entityList.drop(size))
        else {
            securityIndexDayRepository.deleteByRegionAndIdAndType(region, id, type)
            securityIndexDayRepository.saveAllAndFlush(entityList)
        }
    }
}