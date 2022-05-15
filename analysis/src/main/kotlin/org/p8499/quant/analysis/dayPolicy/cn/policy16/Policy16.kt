package org.p8499.quant.analysis.dayPolicy.cn.policy16

import org.p8499.quant.analysis.analyzer.RegionAnalyzer
import org.p8499.quant.analysis.analyzer.SecurityAnalyzer
import org.p8499.quant.analysis.common.floor
import org.p8499.quant.analysis.common.indexDay.*
import org.p8499.quant.analysis.common.indexQuarter.*
import org.p8499.quant.analysis.dayPolicy.Action
import org.p8499.quant.analysis.dayPolicy.Commission
import org.p8499.quant.analysis.dayPolicy.Position
import org.p8499.quant.analysis.dayPolicy.Stage
import org.p8499.quant.analysis.dayPolicy.cn.CNPolicy
import org.p8499.quant.analysis.dayPolicy.cn.CNStatus
import org.p8499.quant.analysis.dayPolicy.cn.ps
import org.p8499.quant.analysis.dayPolicy.cn.weekMacd
import org.p8499.quant.analysis.dayPolicy.common.get
import java.time.LocalDate

//不做调整
open class Policy16(regionAnalyzer: RegionAnalyzer) : CNPolicy(regionAnalyzer) {
    open val securities = regionAnalyzer.securities { true }

    open val slots = 4

    //策略选出的
    var rawTargetSecurities: List<SecurityAnalyzer> = listOf()

    //原持有中并不卖出的
    var keepSecurities: List<SecurityAnalyzer> = listOf()

    //原持有中的计划卖出的
    var sellSecurities: List<SecurityAnalyzer> = listOf()

    //今天可以买入的全新的，数量不限
    var newSecurities: List<SecurityAnalyzer> = listOf()

    //09:15下单，可以在09:25或整个交易日中成交的买入票
    var callingBuySecurities: List<SecurityAnalyzer> = listOf()

    //09:15下单，可以在09:25或整个交易日中成交的卖出票
    var callingSellSecurities: List<SecurityAnalyzer> = listOf()

    //09:25下单，可以在09:30或整个交易日中成交的买入票
    var openingBuySecurities: List<SecurityAnalyzer> = listOf()

    override fun onDay(stage: Stage<CNStatus>, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate) {
        val posSecurities = stage.positions.map(Position::security)
        rawTargetSecurities = find(securities, barDate, informDate, readyDate)
        keepSecurities = posSecurities.filter { isKeeping(it) }
        sellSecurities = posSecurities - keepSecurities
        newSecurities = rawTargetSecurities - posSecurities

        callingBuySecurities = newSecurities.take(slots - posSecurities.size)
        val callingBuySlots = callingBuySecurities.size
        val callingBuyAmount = stage.cash
        callingBuySecurities.onEach {
            val close = it.indexDay["close", barDate].asDouble() ?: 0.0
            val amount = callingBuyAmount / callingBuySlots
            val price = close * 1.05
            val volume = (amount / price).floor(100.0)
            callingCommissions += Commission(Action.BUY, it, price, volume)
        }

        callingSellSecurities = sellSecurities
        callingSellSecurities.onEach {
            val close = it.indexDay["close", barDate].asDouble() ?: 0.0
            val price = close * 0.95
            callingCommissions += Commission(Action.SELL, it, price, stage.positions[it]?.available ?: 0.0)
        }
    }

    override fun hintForOpening(stage: Stage<CNStatus>, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate): String {
        return "最多${slots}个仓位\n" + (newSecurities - callingBuySecurities).take(slots).joinToString("\n") { "BUY\t${it.id}\t${it.indexDay["close", barDate].asDouble()?.let { close -> close * 1.05 } ?: 0.0}" }
    }

    override fun onOpening(stage: Stage<CNStatus>, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate) {
        val posSecurities = stage.positions.map(Position::security)
        openingBuySecurities = (newSecurities - callingBuySecurities).take(slots - posSecurities.size - stage.commissions.filter { it.action == Action.BUY }.size)
        val openingBuySlots = openingBuySecurities.size
        val openingBuyAmount = stage.cash
        openingBuySecurities.onEach {
            val close = it.indexDay["close", barDate].asDouble() ?: 0.0
            val amount = openingBuyAmount / openingBuySlots
            val price = close * 1.05
            val volume = (amount / price).floor(100.0)
            openingCommissions += Commission(Action.BUY, it, price, volume)
        }
    }

    open fun isSafe(security: SecurityAnalyzer, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate): Boolean {
        return every(security.indexQuarter["profitForecast", informDate] gt 0.0, 12).asBool()
                && every(security.indexQuarter["profit", informDate].slice() gt 0.0, 12).asBool()
                && every(security.indexQuarter["profit", informDate].slice().let { it.previousYear() lt it }, 4).asBool()
                && every(security.indexQuarter["profitForecast", informDate].let { ref(it, 1) lt it }, 4).asBool()
                && security.indexQuarter["revenue", informDate].slice().let { it.previousYear() lt it }.asBool()
                && security.indexQuarter["revenueForecast", informDate].let { ref(it, 1) lt it }.asBool()
                && (security.weekMacd(barDate, readyDate) gt 0.0).asBool()
    }

    open fun isKeeping(security: SecurityAnalyzer): Boolean {
        return rawTargetSecurities.indexOf(security).let { it > -1 && it <= slots }
    }

    open fun find(securities: List<SecurityAnalyzer>, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate): List<SecurityAnalyzer> {
        val filtered = securities.filter { isSafe(it, barDate, informDate, readyDate) }
        val ps = filtered.map { it.ps(barDate, informDate) }
        return filtered.indices.sortedWith(compareBy(
                { lod(ps[it], 5).asDouble() ?: Double.MAX_VALUE },
                { (ps[it] / ma(ps[it], 5)).asDouble() ?: Double.MAX_VALUE },
                { ps[it].asDouble() ?: Double.MAX_VALUE })).map(filtered::get)
    }
}