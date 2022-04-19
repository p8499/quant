package org.p8499.quant.analysis.dayPolicy.cn

import org.p8499.quant.analysis.analyzer.RegionAnalyzer
import org.p8499.quant.analysis.dayPolicy.Commission
import org.p8499.quant.analysis.dayPolicy.Policy
import org.p8499.quant.analysis.dayPolicy.Stage
import java.time.LocalDate

abstract class CNPolicy(val regionAnalyzer: RegionAnalyzer) : Policy<CNStatus> {
    override fun proceed(stage: Stage<CNStatus>) {
        val barDate = regionAnalyzer.tradingDates.lastOrNull { it < stage.date }
        val informDate = stage.date.minusDays(1)
        val readyDate = stage.date
        if (barDate != null)
            when (stage.status) {
                CNStatus.OPENING -> onOpening(stage, barDate, informDate, readyDate)
                else -> onDay(stage, barDate, informDate, readyDate)
            }
    }

    override fun hint(stage: Stage<CNStatus>): String {
        val barDate = regionAnalyzer.tradingDates.lastOrNull { it < stage.date }
        val informDate = stage.date.minusDays(1)
        val readyDate = stage.date
        return if (barDate != null)
            when (stage.status) {
                CNStatus.BEFORE -> hintForOpening(stage, barDate, informDate, readyDate)
                else -> ""
            }
        else ""
    }

    abstract fun onOpening(stage: Stage<CNStatus>, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate)

    abstract fun hintForOpening(stage: Stage<CNStatus>, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate): String

    abstract fun onDay(stage: Stage<CNStatus>, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate)

    override fun isTradingDate(date: LocalDate): Boolean = regionAnalyzer.tradingDates.contains(date)

    override val callingCommissions: MutableList<Commission> = mutableListOf()

    override val openingCommissions: MutableList<Commission> = mutableListOf()
}