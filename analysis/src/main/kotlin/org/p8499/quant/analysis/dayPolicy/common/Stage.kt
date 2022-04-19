package org.p8499.quant.analysis.dayPolicy.common

import org.p8499.quant.analysis.dayPolicy.Action
import org.p8499.quant.analysis.dayPolicy.Commission
import org.p8499.quant.analysis.dayPolicy.Stage
import org.p8499.quant.analysis.dayPolicy.Status
import java.time.LocalDate

fun <T : Status> Stage<T>.value(asOf: LocalDate, priceAs: String) = cash +
        commissions.filter { it.action == Action.BUY }.sumOf(Commission::value) +
        positions.value(asOf, priceAs)
