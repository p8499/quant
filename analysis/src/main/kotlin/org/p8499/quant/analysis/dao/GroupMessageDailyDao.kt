package org.p8499.quant.analysis.dao

import org.p8499.quant.analysis.entity.GroupMessageDaily

interface GroupMessageDailyDao {
    fun find(region: String, id: String): List<GroupMessageDaily>

    fun messages(region: String, id: String, limit: Int): List<String>

    fun delete(region: String, id: String): Int
}