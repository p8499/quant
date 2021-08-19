package org.p8499.quant.analysis.dao

import org.p8499.quant.analysis.entity.GroupStock

interface GroupStockDao {
    fun findByGroup(region: String, groupId: String): List<GroupStock>

    fun deleteByGroup(region: String, groupId: String): Int
}