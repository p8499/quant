package org.p8499.quant.analysis.dao

import org.p8499.quant.analysis.entity.SecurityIndexQuarter
import java.time.LocalDate

interface SecurityIndexQuarterDao {
    fun find(region: String, id: String, type: String): List<SecurityIndexQuarter>

    fun find(region: String, id: String, type: String, from: LocalDate, to: LocalDate): List<SecurityIndexQuarter>

    fun size(region: String, id: String, type: String): Int

    fun firstOrEmpty(region: String, id: String, type: String): List<SecurityIndexQuarter>

    fun lastOrEmpty(region: String, id: String, type: String): List<SecurityIndexQuarter>

    fun quarters(region: String, id: String, type: String, limit: Int): List<Int>

    fun values(region: String, id: String, type: String, limit: Int): List<Double?>

    fun publishes(region: String, id: String, type: String, limit: Int): List<LocalDate?>
}