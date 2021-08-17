package org.p8499.quant.analysis.dao

import org.p8499.quant.analysis.entity.GroupIndexDaily

interface GroupIndexDailyDao {
    fun find(region: String, id: String, kpi: String): List<GroupIndexDaily>

    fun deleteById(id: String): Int
}