package org.p8499.quant.analysis.dao

import org.p8499.quant.analysis.entity.SecurityMessageQuarter
import java.time.LocalDate

interface SecurityMessageQuarterDao {
    fun find(region: String, id: String, type: String): List<SecurityMessageQuarter>

    fun find(region: String, id: String, type: String, from: LocalDate, to: LocalDate): List<SecurityMessageQuarter>

    fun size(region: String, id: String, type: String): Int

    fun firstOrEmpty(region: String, id: String, type: String): List<SecurityMessageQuarter>

    fun lastOrEmpty(region: String, id: String, type: String): List<SecurityMessageQuarter>

    fun quarters(region: String, id: String, type: String, limit: Int): List<Int>

    fun values(region: String, id: String, type: String, limit: Int): List<String?>

    fun publishes(region: String, id: String, type: String, limit: Int): List<LocalDate?>
}