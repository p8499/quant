package org.p8499.quant.analysis.dao

import org.p8499.quant.analysis.entity.StockMessageDaily

interface StockMessageDailyDao {
    fun find(region: String, id: String): List<StockMessageDaily>

    fun messages(region: String, id: String, limit: Int): List<String>

    fun delete(region: String, id: String): Int
}