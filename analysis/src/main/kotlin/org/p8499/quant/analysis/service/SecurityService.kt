package org.p8499.quant.analysis.service

import org.p8499.quant.analysis.entity.Security
import org.p8499.quant.analysis.repository.SecurityRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class SecurityService {
    @Autowired
    protected lateinit var securityRepository: SecurityRepository

    fun find(region: String): List<Security> = securityRepository.find(region)

    fun tradingDates(region: String): List<LocalDate> = securityRepository.tradingDates(region)

    fun firstDay(region: String, id: String): LocalDate? = securityRepository.firstDay(region, id)

    fun lastDay(region: String, id: String): LocalDate? = securityRepository.lastDay(region, id)

    fun save(entity: Security): Security = securityRepository.save(entity)

    fun delete(region: String, id: String): Int = securityRepository.delete(region, id)
}