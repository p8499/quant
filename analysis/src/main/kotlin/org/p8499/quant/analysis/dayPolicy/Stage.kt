package org.p8499.quant.analysis.dayPolicy

import org.p8499.quant.analysis.analyzer.SecurityAnalyzer
import java.time.LocalDate

interface Stage<T : Status> {
    var date: LocalDate
    var status: T
    var cash: Double
    val positions: MutableList<Position>
    val commissions: MutableList<Commission>
    val transactions: MutableList<Transaction>
    val snapshots: MutableList<Snapshot<T>>
    fun run(toDate: LocalDate, toStatus: T, policy: Policy<T>)
    fun value(): Double
    fun positionValue(security: SecurityAnalyzer): Double
}


