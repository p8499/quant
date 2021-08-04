package org.p8499.quant.tushare.dao

import org.p8499.quant.tushare.entity.GroupStock

interface GroupStockDao {
    fun findByGroupId(groupId: String): List<GroupStock>
}