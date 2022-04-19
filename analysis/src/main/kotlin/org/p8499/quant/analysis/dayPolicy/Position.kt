package org.p8499.quant.analysis.dayPolicy

import org.p8499.quant.analysis.analyzer.SecurityAnalyzer

class Position(val security: SecurityAnalyzer, var available: Double, var unavailable: Double, var cost: Double) {
    val volume: Double get() = available + unavailable
}
