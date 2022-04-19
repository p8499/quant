package org.p8499.quant.analysis.dayPolicy.cn.policy13

import org.p8499.quant.analysis.analyzer.RegionAnalyzer
import org.p8499.quant.analysis.analyzer.SecurityAnalyzer
import org.p8499.quant.analysis.common.floor
import org.p8499.quant.analysis.common.indexDay.*
import org.p8499.quant.analysis.common.indexQuarter.*
import org.p8499.quant.analysis.common.let
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

open class Policy13(regionAnalyzer: RegionAnalyzer) : CNPolicy(regionAnalyzer) {
    open val securities = regionAnalyzer.securities { true }

    open val slots = 4

    //策略选出的
    var rawTargetSecurities: List<SecurityAnalyzer> = listOf()

    //原持有中并不卖出的
    var keepSecurities: List<SecurityAnalyzer> = listOf()

    //原持有的中的并不卖出的中的可以补仓的
    var overweightSecurities: List<SecurityAnalyzer> = listOf()

    //原持有的中的并不卖出的中的保持仓位的
    var holdingweightSecurities: List<SecurityAnalyzer> = listOf()

    //原持有中的计划卖出的
    var sellSecurities: List<SecurityAnalyzer> = listOf()

    //今天可以买入的，包含全新的和补仓的，数量不限
    var targetSecurities: List<SecurityAnalyzer> = listOf()

    //今天可以买入的全新的，数量不限
    var newSecurities: List<SecurityAnalyzer> = listOf()

    //09:15下单，可以在09:25或整个交易日中成交的买入单，包含全新的和补仓的
    var callingBuyOrAdjustSecurities: List<SecurityAnalyzer> = listOf()

    //09:15下单，可以在09:25或整个交易日中成交的卖单
    var callingSellSecurities: List<SecurityAnalyzer> = listOf()

    //09:25下单，可以在09:30或整个交易日中成交的买单
    var openingBuySecurities: List<SecurityAnalyzer> = listOf()

    override fun onDay(stage: Stage<CNStatus>, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate) {
        val posSecurities = stage.positions.map(Position::security)
        rawTargetSecurities = find(securities, barDate, informDate, readyDate)
        keepSecurities = posSecurities.filter { isKeeping(it) }
        overweightSecurities = keepSecurities.filter { isOverweight(it) }
        holdingweightSecurities = keepSecurities - overweightSecurities//
        sellSecurities = posSecurities - keepSecurities
        targetSecurities = (rawTargetSecurities - holdingweightSecurities - sellSecurities)//
        newSecurities = targetSecurities - overweightSecurities

        val callingNewSecurities = newSecurities.take(slots - posSecurities.size)//
        val callingOverweightSecurities = overweightSecurities
        callingBuyOrAdjustSecurities = callingNewSecurities + callingOverweightSecurities
        val callingBuyOrAdjustSlots = callingBuyOrAdjustSecurities.size
        val callingBuyOrAdjustAmount = stage.cash + callingOverweightSecurities.sumOf { let(it.indexDay["close", barDate].asDouble(), stage.positions[it]?.volume) { price, volume -> price * volume } ?: 0.0 }
        callingBuyOrAdjustSecurities.onEach {
            val close = it.indexDay["close", barDate].asDouble() ?: 0.0
            val currentVolume = stage.positions[it]?.volume ?: 0.0
            val currentAmount = close * currentVolume
            val targetAmount = callingBuyOrAdjustAmount / callingBuyOrAdjustSlots
            val amount = targetAmount - currentAmount
            if (amount > 0) {
                val price = close * 1.05
                val volume = (amount / price).floor(100.0)
                if (volume > 0)
                    callingCommissions += Commission(Action.BUY, it, price, volume)
            } else if (amount < 0) {
                val price = close * 0.95
                val volume = (-amount / price).floor(100.0)
                if (volume > 0)
                    callingCommissions += Commission(Action.SELL, it, price, volume)
            }
        }

        callingSellSecurities = sellSecurities
        callingSellSecurities.onEach {
            val close = it.indexDay["close", barDate].asDouble() ?: 0.0
            val price = close * 0.95
            callingCommissions += Commission(Action.SELL, it, price, stage.positions[it]?.available ?: 0.0)
        }
    }

    override fun hintForOpening(stage: Stage<CNStatus>, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate): String {
        val openingBuySlogsMin = slots - keepSecurities.size - sellSecurities.size - newSecurities.size
        val openingBuySlotsMax = slots - keepSecurities.size
        val brief = "可能空余$openingBuySlogsMin ~ $openingBuySlotsMax 个仓位\n"
        val list = (targetSecurities - callingBuyOrAdjustSecurities).joinToString("\n") { "BUY\t${it.id}\t${it.indexDay["close", barDate].asDouble() ?: 0.0}" }
        return "$brief$list"
    }

    override fun onOpening(stage: Stage<CNStatus>, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate) {
        val posSecurities = stage.positions.map(Position::security)
        openingBuySecurities = (targetSecurities - callingBuyOrAdjustSecurities).take(slots - posSecurities.size)
        val openingBuySlots = openingBuySecurities.size
        val openingBuyAmount = stage.cash
        openingBuySecurities.onEach {
            val close = it.indexDay["close", barDate].asDouble() ?: 0.0
            val amount = openingBuyAmount / openingBuySlots
            val price = close
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
        return rawTargetSecurities.indexOf(security).let { it > -1 && it <= slots * 2 }
    }

    open fun isOverweight(security: SecurityAnalyzer): Boolean {
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