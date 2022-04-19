package org.p8499.quant.analysis.dayPolicy

import org.p8499.quant.analysis.analyzer.SecurityAnalyzer
import java.time.LocalDateTime

class Transaction(val time: LocalDateTime, val action: Action, val security: SecurityAnalyzer, val price: Double, val volume: Double, val pl: Double?, val plPercent: Double?)
