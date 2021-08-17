package org.p8499.quant.analysis.dao

import org.p8499.quant.analysis.entity.GroupStock

interface GroupStockDao {
    fun findByGroupId(groupId: String): List<GroupStock>

    fun deleteByGroupId(groupId: String): Int
}