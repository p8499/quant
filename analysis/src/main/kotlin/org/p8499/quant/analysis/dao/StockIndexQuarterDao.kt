package org.p8499.quant.analysis.dao

import java.time.LocalDate

interface StockIndexQuarterDao {
    fun dates(region: String, id: String, limit: Int): List<LocalDate>

    fun values(region: String, id: String, kpi: String, limit: Int): List<Double?>

    fun publishes(region: String, id: String, kpi: String, limit: Int): List<LocalDate?>

    fun delete(region: String, id: String): Int
}