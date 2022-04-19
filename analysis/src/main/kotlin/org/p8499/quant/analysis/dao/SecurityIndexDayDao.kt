package org.p8499.quant.analysis.dao

import org.p8499.quant.analysis.entity.SecurityIndexDay
import java.time.LocalDate

interface SecurityIndexDayDao {
    fun find(region: String, id: String, type: String): List<SecurityIndexDay>

    fun find(region: String, id: String, type: String, from: LocalDate, to: LocalDate): List<SecurityIndexDay>

    fun size(region: String, id: String, type: String): Int

    fun firstOrNull(region: String, id: String, type: String): SecurityIndexDay?

    fun lastOrNull(region: String, id: String, type: String): SecurityIndexDay?

    fun dates(region: String, id: String, type: String, limit: Int): List<LocalDate>

    fun values(region: String, id: String, type: String, limit: Int): List<Double?>
}