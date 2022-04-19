package org.p8499.quant.analysis.service

import org.p8499.quant.analysis.entity.SecurityMessageQuarter
import org.p8499.quant.analysis.repository.SecurityMessageQuarterRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class SecurityMessageQuarterService {
    @Autowired
    protected lateinit var securityMessageQuarterRepository: SecurityMessageQuarterRepository

    fun find(region: String, id: String, type: String): List<SecurityMessageQuarter> = securityMessageQuarterRepository.find(region, id, type)

    fun find(region: String, id: String, type: String, from: LocalDate, to: LocalDate) = securityMessageQuarterRepository.find(region, id, type, from, to)

    fun size(region: String, id: String, type: String): Int = securityMessageQuarterRepository.size(region, id, type)

    fun firstOrEmpty(region: String, id: String, type: String): List<SecurityMessageQuarter> = securityMessageQuarterRepository.firstOrEmpty(region, id, type)

    fun lastOrEmpty(region: String, id: String, type: String): List<SecurityMessageQuarter> = securityMessageQuarterRepository.lastOrEmpty(region, id, type)

    fun quarters(region: String, id: String, type: String, limit: Int): List<Int> = securityMessageQuarterRepository.quarters(region, id, type, limit)

    fun values(region: String, id: String, type: String, limit: Int): List<String?> = securityMessageQuarterRepository.values(region, id, type, limit)

    fun publishes(region: String, id: String, type: String, limit: Int): List<LocalDate?> = securityMessageQuarterRepository.publishes(region, id, type, limit)

    @Transactional
    fun saveAll(region: String, id: String, type: String, entityList: List<SecurityMessageQuarter>): List<SecurityMessageQuarter> {
        var append = false
        val size = size(region, id, type)
        val first = firstOrEmpty(region, id, type)
        val last = lastOrEmpty(region, id, type)
        if (size <= entityList.size) {
            val correspondingFirst = entityList.take(first.size)
            val correspondingLast = entityList.subList(size - last.size, size)
            append = first.containsAll(correspondingFirst) && correspondingFirst.containsAll(first) && last.containsAll(correspondingLast) && correspondingLast.containsAll(last)
        }
        return if (append)
            securityMessageQuarterRepository.saveAllAndFlush(entityList.drop(size))
        else {
            securityMessageQuarterRepository.deleteByRegionAndIdAndType(region, id, type)
            securityMessageQuarterRepository.saveAllAndFlush(entityList)
        }
    }
}