package org.p8499.quant.analysis.dao

import org.p8499.quant.analysis.entity.Group

interface GroupDao {
    fun find(region: String): List<Group>

    fun delete(region: String, id: String): Int
}