package org.p8499.quant.analysis.policy

import java.time.LocalDate

interface Policy {
    fun dates(): List<LocalDate>
    fun select(): List<Security>
    fun extend(security: Security)
    fun proceed(stage: Stage, date: LocalDate)
}