package org.p8499.quant.analysis.dayPolicy.common

import org.p8499.quant.analysis.dayPolicy.Commission

val Commission.value get() = price * volume

val List<Commission>.value get() = sumOf(Commission::value)
