package org.p8499.quant.analysis.dao

import org.p8499.quant.analysis.entity.Stock

interface StockDao {
    fun find(region: String): List<Stock>

    fun findByGroup(region: String, groupId: String): List<Stock>

    fun delete(region: String, id: String): Int
}