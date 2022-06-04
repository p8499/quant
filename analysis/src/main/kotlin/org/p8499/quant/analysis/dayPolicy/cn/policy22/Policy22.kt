package org.p8499.quant.analysis.dayPolicy.cn.policy22

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
import org.p8499.quant.analysis.dayPolicy.cn.*
import org.p8499.quant.analysis.dayPolicy.common.get
import java.time.LocalDate
import kotlin.math.max
import kotlin.math.min

//基于21，设定卖出价
open class Policy22(regionAnalyzer: RegionAnalyzer) : CNPolicy(regionAnalyzer) {
    open val securities = regionAnalyzer.securities { true }.onEach { it.mcst() }

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
            val optimistic = (it.indexDay["close", barDate] lt it.indexDay["mcst", barDate]).asBool()
            val price = if (optimistic) close * 1.1 else close
            val volume = (amount / price).floor(100.0)
            callingCommissions += Commission(Action.BUY, it, price, volume)
        }

        callingSellSecurities = sellSecurities
        callingSellSecurities.onEach {
            val close = it.indexDay["close", barDate].asDouble() ?: 0.0
            val price = close * 0.9
            callingCommissions += Commission(Action.SELL, it, price, stage.positions[it]?.available ?: 0.0)
        }

        posSecurities.onEach {
            val price = sellPrice(it, barDate, informDate, readyDate)
            if (price !== null)
                callingCommissions += Commission(Action.SELL, it, price, stage.positions[it]?.available ?: 0.0)
        }
    }

    override fun hintForOpening(stage: Stage<CNStatus>, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate): String {
        val sb = StringBuilder()
        sb.append("最多${slots}个仓位\n")
        openingBuySecurities = newSecurities - callingBuySecurities
        openingBuySecurities.forEach {
            val optimistic = (it.indexDay["close", barDate] lt it.indexDay["mcst", barDate]).asBool()
            sb.append("${it.id} ${if (optimistic) "取高" else "取低"}\n")
        }
        return sb.toString()
    }

    override fun onOpening(stage: Stage<CNStatus>, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate) {
        val posSecurities = stage.positions.map(Position::security)
        openingBuySecurities = (newSecurities - callingBuySecurities).take(slots - posSecurities.size - stage.commissions.filter { it.action == Action.BUY }.size)
        val openingBuySlots = openingBuySecurities.size
        val openingBuyAmount = stage.cash
        openingBuySecurities.onEach {
            val amount = openingBuyAmount / openingBuySlots
            val close = it.indexDay["close", barDate].asDouble() ?: 0.0
            val optimistic = (it.indexDay["close", barDate] lt it.indexDay["mcst", barDate]).asBool()
            val price = (it.indexDay["open", readyDate].asDouble() ?: 0.0).let { o -> if (optimistic) max(o, close) else min(o, close) }
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
                //SOLUTION: byGroup比byWeek表现更好
                && (security.groupMacd(barDate, 5) gt 0.0).asBool()
    }

    open fun isKeeping(security: SecurityAnalyzer): Boolean {
        return rawTargetSecurities.indexOf(security).let { it > -1 && it <= slots }
    }

    open fun find(securities: List<SecurityAnalyzer>, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate): List<SecurityAnalyzer> {
        val filtered = securities.filter { isSafe(it, barDate, informDate, readyDate) }
        val ps = filtered.map { it.ps(barDate, informDate) }
        return filtered.indices.sortedWith(compareBy(
                //SOLUTION: 去除lod后表现稍好
//                { lod(ps[it], 5).asDouble() ?: Double.MAX_VALUE },
                { (ps[it] / ma(ps[it], 5)).asDouble() ?: Double.MAX_VALUE },
                { ps[it].asDouble() ?: Double.MAX_VALUE })).map(filtered::get)
    }

    open fun sellPrice(security: SecurityAnalyzer, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate): Double? {
        val close = security.indexDay["close", barDate]
//        val open = security.indexDay["open", barDate]
//        val lower = min(open, close)
//        val higher = max(open, close)
        val lower = security.indexDay["low", barDate]
        val higher = security.indexDay["high", barDate]
        val price = ref(lower, barslast(ref(lower, 1) gt higher) + 1.0).asDouble()
        return let(price, close.asDouble()) { a, b -> if (a > b) a else null }
    }
}