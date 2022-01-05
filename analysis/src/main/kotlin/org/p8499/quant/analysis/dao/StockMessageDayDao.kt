package org.p8499.quant.analysis.dao

import org.p8499.quant.analysis.entity.StockMessageDay

interface StockMessageDayDao {
    fun find(region: String, id: String): List<StockMessageDay>

    fun messages(region: String, id: String, limit: Int): List<String>

    fun delete(region: String, id: String): Int
}