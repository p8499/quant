package org.p8499.quant.analysis.dao

interface StockDao {
    fun deleteById(id: String): Int
}