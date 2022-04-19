package org.p8499.quant.analysis.dao

import org.p8499.quant.analysis.entity.Security
import org.springframework.data.jpa.repository.Modifying
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

interface SecurityDao {
    fun find(region: String): List<Security>

    fun tradingDates(region: String): List<LocalDate>

    fun firstDay(region: String, id: String): LocalDate?

    fun lastDay(region: String, id: String): LocalDate?

    @Transactional
    @Modifying
    fun delete(region: String, id: String): Int
}