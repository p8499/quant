package org.p8499.quant.analysis.dao

import org.p8499.quant.analysis.entity.StockIndexDaily

interface StockIndexDailyDao {
    fun find(region: String, id: String, kpi: String): List<StockIndexDaily>

    fun delete(region: String, id: String): Int
}