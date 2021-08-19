package org.p8499.quant.analysis.dao

interface GroupDao {
    fun delete(region: String,id: String): Int
}