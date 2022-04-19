package org.p8499.quant.analysis.dayPolicy

import org.p8499.quant.analysis.analyzer.SecurityAnalyzer

class Commission(val action: Action, val security: SecurityAnalyzer, val price: Double, val volume: Double)
