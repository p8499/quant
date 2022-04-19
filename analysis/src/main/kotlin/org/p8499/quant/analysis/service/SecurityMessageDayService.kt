package org.p8499.quant.analysis.service

import org.p8499.quant.analysis.entity.SecurityMessageDay
import org.p8499.quant.analysis.repository.SecurityMessageDayRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class SecurityMessageDayService {
    @Autowired
    protected lateinit var securityMessageDayRepository: SecurityMessageDayRepository

    fun find(region: String, id: String, type: String): List<SecurityMessageDay> = securityMessageDayRepository.find(region, id, type)

    fun find(region: String, id: String, type: String, from: LocalDate, to: LocalDate) = securityMessageDayRepository.find(region, id, type, from, to)

    fun size(region: String, id: String, type: String): Int = securityMessageDayRepository.size(region, id, type)

    fun firstOrNull(region: String, id: String, type: String): SecurityMessageDay? = securityMessageDayRepository.firstOrNull(region, id, type)

    fun lastOrNull(region: String, id: String, type: String): SecurityMessageDay? = securityMessageDayRepository.lastOrNull(region, id, type)

    fun dates(region: String, id: String, type: String, limit: Int): List<LocalDate> = securityMessageDayRepository.dates(region, id, type, limit)

    fun values(region: String, id: String, type: String, limit: Int): List<String?> = securityMessageDayRepository.values(region, id, type, limit)

    @Transactional
    fun saveAll(region: String, id: String, type: String, entityList: List<SecurityMessageDay>): List<SecurityMessageDay> {
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
            securityMessageDayRepository.saveAllAndFlush(entityList.drop(size))
        else {
            securityMessageDayRepository.deleteByRegionAndIdAndType(region, id, type)
            securityMessageDayRepository.saveAllAndFlush(entityList)
        }
    }
}