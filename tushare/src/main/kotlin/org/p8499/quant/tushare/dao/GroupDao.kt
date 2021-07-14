package org.p8499.quant.tushare.dao

import org.p8499.quant.tushare.entity.Group

interface GroupDao {
    fun findByType(type: Group.Type): List<Group>
}