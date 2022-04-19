package org.p8499.quant.analysis.dayPolicy

import java.time.LocalDate

class Snapshot<T : Status>(
        val date: LocalDate,
        val status: T,
        val cash: Double,
        val positions: List<SnapshotPosition>
)