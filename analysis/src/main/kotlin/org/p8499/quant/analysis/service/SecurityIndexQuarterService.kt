package org.p8499.quant.analysis.service

import org.p8499.quant.analysis.entity.SecurityIndexQuarter
import org.p8499.quant.analysis.repository.SecurityIndexQuarterRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class SecurityIndexQuarterService {
    @Autowired
    protected lateinit var securityIndexQuarterRepository: SecurityIndexQuarterRepository

    fun find(region: String, id: String, type: String): List<SecurityIndexQuarter> = securityIndexQuarterRepository.find(region, id, type)

    fun find(region: String, id: String, type: String, from: LocalDate, to: LocalDate) = securityIndexQuarterRepository.find(region, id, type, from, to)

    fun size(region: String, id: String, type: String): Int = securityIndexQuarterRepository.size(region, id, type)

    fun firstOrEmpty(region: String, id: String, type: String): List<SecurityIndexQuarter> = securityIndexQuarterRepository.firstOrEmpty(region, id, type)

    fun lastOrEmpty(region: String, id: String, type: String): List<SecurityIndexQuarter> = securityIndexQuarterRepository.lastOrEmpty(region, id, type)

    fun quarters(region: String, id: String, type: String, limit: Int): List<Int> = securityIndexQuarterRepository.quarters(region, id, type, limit)

    fun values(region: String, id: String, type: String, limit: Int): List<Double?> = securityIndexQuarterRepository.values(region, id, type, limit)

    fun publishes(region: String, id: String, type: String, limit: Int): List<LocalDate?> = securityIndexQuarterRepository.publishes(region, id, type, limit)

    @Transactional
    fun saveAll(region: String, id: String, type: String, entityList: List<SecurityIndexQuarter>): List<SecurityIndexQuarter> {
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
            securityIndexQuarterRepository.saveAllAndFlush(entityList.drop(size))
        else {
            securityIndexQuarterRepository.deleteByRegionAndIdAndType(region, id, type)
            securityIndexQuarterRepository.saveAllAndFlush(entityList)
        }
    }
}