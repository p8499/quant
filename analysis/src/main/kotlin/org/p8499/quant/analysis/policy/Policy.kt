package org.p8499.quant.analysis.policy

import java.time.LocalDate

abstract class Policy {
    protected abstract fun dates(): List<LocalDate>
    protected abstract fun select(): List<Security>
    protected abstract fun extend(security: Security)
    open fun pre(stage: Stage, date: LocalDate) {}
    abstract fun proceed(stage: Stage, date: LocalDate)
    open fun post(stage: Stage, date: LocalDate) {}

    val securities by lazy { select().onEach(::extend) }
    val tradingDates by lazy { dates() }

    fun isTradingDate(currentDate: LocalDate): Boolean {
        return tradingDates.indexOf(currentDate) > -1
    }

    fun previousTradingDate(currentDate: LocalDate, initDate: LocalDate): LocalDate? {
        var previousDate = currentDate
        do {
            previousDate = previousDate.minusDays(1)
            if (isTradingDate(previousDate))
                return previousDate
        } while (previousDate > initDate)
        return null
    }

    fun nextTradingDate(currentDate: LocalDate, finalDate: LocalDate): LocalDate? {
        var nextDate = currentDate
        do {
            nextDate = nextDate.plusDays(1)
            if (isTradingDate(nextDate))
                return nextDate
        } while (nextDate < finalDate)
        return null
    }

    fun tradingDates(from: LocalDate, to: LocalDate): List<LocalDate> {
        val fromIndex = tradingDates.indexOfFirst { it >= from }
        val toIndex = tradingDates.indexOfLast { it <= to }
        return if (fromIndex > -1 && toIndex > -1) tradingDates.subList(fromIndex, toIndex + 1) else listOf()
    }
}