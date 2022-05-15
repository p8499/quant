package org.p8499.quant.analysis.dayPolicy.cn.policy20

import org.p8499.quant.analysis.analyzer.RegionAnalyzer
import org.p8499.quant.analysis.analyzer.SecurityAnalyzer
import org.p8499.quant.analysis.common.floor
import org.p8499.quant.analysis.common.indexDay.asDouble
import org.p8499.quant.analysis.common.indexDay.div
import org.p8499.quant.analysis.common.indexDay.ma
import org.p8499.quant.analysis.common.indexDay.times
import org.p8499.quant.analysis.common.indexQuarter.*
import org.p8499.quant.analysis.dayPolicy.Action
import org.p8499.quant.analysis.dayPolicy.Commission
import org.p8499.quant.analysis.dayPolicy.Position
import org.p8499.quant.analysis.dayPolicy.Stage
import org.p8499.quant.analysis.dayPolicy.cn.CNStatus
import org.p8499.quant.analysis.dayPolicy.cn.ps
import org.p8499.quant.analysis.dayPolicy.common.get
import java.time.LocalDate

//不做调整，设定卖出
open class Policy20A(regionAnalyzer: RegionAnalyzer) : Policy20(regionAnalyzer) {
    override val securities = regionAnalyzer.securities { true }
    override fun onDay(stage: Stage<CNStatus>, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate) {
        val posSecurities = stage.positions.map(Position::security)
        rawTargetSecurities = find(securities, barDate, informDate, readyDate)
        newSecurities = rawTargetSecurities - posSecurities

        callingBuySecurities = newSecurities.take(slots - posSecurities.size)
        val callingBuySlots = callingBuySecurities.size
        val callingBuyAmount = stage.cash
        callingBuySecurities.onEach {
            val amount = callingBuyAmount / callingBuySlots
            val price = buyPrice(it, barDate, informDate, readyDate) ?: 0.0
            val volume = (amount / price).floor(100.0)
            callingCommissions += Commission(Action.BUY, it, price, volume)
        }

        callingSellSecurities = posSecurities
        callingSellSecurities.onEach {
            val price = sellPrice(it, barDate, informDate, readyDate) ?: 0.0
            callingCommissions += Commission(Action.SELL, it, price, stage.positions[it]?.available ?: 0.0)
        }
    }

    override fun hintForOpening(stage: Stage<CNStatus>, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate): String = ""

    override fun onOpening(stage: Stage<CNStatus>, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate) = Unit

    override fun isSafe(security: SecurityAnalyzer, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate): Boolean {
        return every(security.indexQuarter["profitForecast", informDate] gt 0.0, 12).asBool()
                && every(security.indexQuarter["profit", informDate].slice() gt 0.0, 12).asBool()
                && every(security.indexQuarter["profit", informDate].slice().let { it.previousYear() lt it }, 4).asBool()
                && every(security.indexQuarter["profitForecast", informDate].let { ref(it, 1) lt it }, 4).asBool()
                && security.indexQuarter["revenue", informDate].slice().let { it.previousYear() lt it }.asBool()
                && security.indexQuarter["revenueForecast", informDate].let { ref(it, 1) lt it }.asBool()
    }

    override fun find(securities: List<SecurityAnalyzer>, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate): List<SecurityAnalyzer> {
        val filtered = securities.filter { isSafe(it, barDate, informDate, readyDate) }
        println("$readyDate\t${filtered.firstOrNull()?.id}\t${filtered.firstOrNull()?.let { buyPrice(it, barDate, informDate, readyDate) }}")
        val ps = filtered.map { it.ps(barDate, informDate) }
        return filtered.indices.sortedWith(compareBy(
                { (ps[it] / ma(ps[it], 5)).asDouble() ?: Double.MAX_VALUE })).map(filtered::get)
    }

    override fun sellPrice(security: SecurityAnalyzer, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate): Double? {
        return (ma(security.ps(barDate, informDate), 5) * (security.indexQuarter.flatten["revenueForecast", barDate, informDate] / security.indexDay["totalShare", barDate])).asDouble()
    }

    override fun buyPrice(security: SecurityAnalyzer, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate): Double? {
        return (ma(security.ps(barDate, informDate), 5) * (security.indexQuarter.flatten["revenueForecast", barDate, informDate] / security.indexDay["totalShare", barDate])).asDouble()
    }
}