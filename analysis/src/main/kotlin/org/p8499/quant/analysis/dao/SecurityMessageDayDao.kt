package org.p8499.quant.analysis.dao

import org.p8499.quant.analysis.entity.SecurityMessageDay
import java.time.LocalDate

interface SecurityMessageDayDao {
    fun find(region: String, id: String, type: String): List<SecurityMessageDay>

    fun find(region: String, id: String, type: String, from: LocalDate, to: LocalDate): List<SecurityMessageDay>

    fun size(region: String, id: String, type: String): Int

    fun firstOrNull(region: String, id: String, type: String): SecurityMessageDay?

    fun lastOrNull(region: String, id: String, type: String): SecurityMessageDay?

    fun dates(region: String, id: String, type: String, limit: Int): List<LocalDate>

    fun values(region: String, id: String, type: String, limit: Int): List<String?>
}