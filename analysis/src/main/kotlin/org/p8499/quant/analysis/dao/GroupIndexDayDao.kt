package org.p8499.quant.analysis.dao

import org.p8499.quant.analysis.entity.GroupIndexDay
import java.time.LocalDate

interface GroupIndexDayDao {
    fun find(region: String, id: String, kpi: String): List<GroupIndexDay>

    fun size(region: String, id: String): Int

    fun dates(region: String, id: String, limit: Int): List<LocalDate>

    fun values(region: String, id: String, kpi: String, limit: Int): List<Double?>

    fun tradingDates(region: String): List<LocalDate>

    fun delete(region: String, id: String): Int
}