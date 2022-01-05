package org.p8499.quant.analysis.dao

import org.p8499.quant.analysis.entity.GroupMessageDay

interface GroupMessageDayDao {
    fun find(region: String, id: String): List<GroupMessageDay>

    fun messages(region: String, id: String, limit: Int): List<String>

    fun delete(region: String, id: String): Int
}