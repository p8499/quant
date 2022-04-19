package org.p8499.quant.analysis.dayPolicy.cn.policy19

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
import org.p8499.quant.analysis.entity.SecurityIndexDay
import java.time.LocalDate
import kotlin.math.min

//在18的基础上，放弃callingBuy，只通过openingBuy按照开盘价重排序
open class Policy19(regionAnalyzer: RegionAnalyzer) : CNPolicy(regionAnalyzer) {
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

        val callingNewSecurities = listOf<SecurityAnalyzer>()//newSecurities.take(slots - posSecurities.size)
        val callingNewSlots = callingNewSecurities.size
        val callingOverweightSecurities = overweightSecurities
        val callingOverweightSlots = callingOverweightSecurities.size
        callingBuyOrAdjustSecurities = callingNewSecurities + callingOverweightSecurities
        val callingBuyOrAdjustSlots = callingBuyOrAdjustSecurities.size
        val callingBuyOrAdjustAmount = stage.cash + callingOverweightSecurities.sumOf { let(it.indexDay["close", barDate].asDouble(), stage.positions[it]?.volume) { price, volume -> price * volume } ?: 0.0 }
        var callingCash = stage.cash
        callingNewSecurities.onEach {
            val close = it.indexDay["close", barDate].asDouble() ?: 0.0
            val amount = min(callingBuyOrAdjustAmount / callingBuyOrAdjustSlots, callingCash)
            val price = close * 1.05
            val volume = (amount / price).floor(100.0)
            callingCommissions += Commission(Action.BUY, it, price, volume)
            callingCash -= price * volume
        }
        if (callingCash > 0) {
            callingNewSecurities.onEach {
                val close = it.indexDay["close", barDate].asDouble() ?: 0.0
                val amount = callingCash / callingBuyOrAdjustSlots
                val price = close * 1.05
                val volume = (amount / price).floor(100.0)
                if (volume > 0)
                    callingCommissions += Commission(Action.BUY, it, price, volume)
            }
            callingOverweightSecurities.onEach {
                val close = it.indexDay["close", barDate].asDouble() ?: 0.0
                val amount = callingCash / callingBuyOrAdjustSlots
                val price = close * 1.05
                val volume = (amount / price).floor(100.0)
                if (volume > 0)
                    callingCommissions += Commission(Action.BUY, it, price, volume)
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
        openingBuySecurities = openingSort(targetSecurities - callingBuyOrAdjustSecurities, barDate, informDate, readyDate).take(slots - posSecurities.size)
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

    // 必须在readyDate的opening状态之后运行，否则就是未来函数（得到未来的开盘价）
    fun SecurityAnalyzer.openingPs(barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate): List<SecurityIndexDay> {
        val rate = let(indexDay["open", readyDate].asDouble(), indexDay["close", barDate].asDouble()) { o, c -> (o - c) / c }
        val new = indexDay["close", barDate].last().also {
            it.date = readyDate
            it.value = let(it.value, rate) { c, r -> c * (1 + r) }
        }
        val closeList = indexDay["close", barDate] + new
        val revenueForecastList = indexQuarter.flatten["revenueForecast", barDate, informDate].let { it + it.last() }
        val totalShareList = indexDay["totalShare", barDate].let { it + it.last() }
        return closeList / (revenueForecastList / totalShareList)
    }

    // 必须在readyDate的opening状态之后运行，否则就是未来函数（得到未来的开盘价）
    // 实盘时，输入数字模拟
    open fun openingSort(securities: List<SecurityAnalyzer>, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate): List<SecurityAnalyzer> {
        val openingPs = securities.map { it.openingPs(barDate, informDate, readyDate) }
        return securities.indices.sortedWith(compareBy(
                { lod(openingPs[it], 5).asDouble() ?: Double.MAX_VALUE },
                { (openingPs[it] / ma(openingPs[it], 5)).asDouble() ?: Double.MAX_VALUE },
                { openingPs[it].asDouble() ?: Double.MAX_VALUE })).map(securities::get)
    }
}