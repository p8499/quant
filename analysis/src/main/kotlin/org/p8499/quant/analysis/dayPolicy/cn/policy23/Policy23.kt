package org.p8499.quant.analysis.dayPolicy.cn.policy23

import org.p8499.quant.analysis.analyzer.RegionAnalyzer
import org.p8499.quant.analysis.analyzer.SecurityAnalyzer
import org.p8499.quant.analysis.analyzer.SecurityIndexDayAnalyzer
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
import org.p8499.quant.analysis.dayPolicy.common.get
import org.p8499.quant.analysis.entity.SecurityIndexDay
import org.slf4j.LoggerFactory
import java.time.LocalDate
import kotlin.math.max

//支撑点以下的，止盈在支撑点；支撑点以上的，止盈设在mcst
open class Policy23(regionAnalyzer: RegionAnalyzer) : CNPolicy(regionAnalyzer) {
    protected val logger by lazy { LoggerFactory.getLogger(javaClass) }
    open val securities = regionAnalyzer.securities { true }

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
    override fun onOpening(stage: Stage<CNStatus>, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate) {}

    override fun hintForOpening(stage: Stage<CNStatus>, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate): String = ""

    override fun onDay(stage: Stage<CNStatus>, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate) {
        val posSecurities = stage.positions.map(Position::security)
        rawTargetSecurities = find(securities, barDate, informDate, readyDate)
        newSecurities = rawTargetSecurities - posSecurities
        sellSecurities = posSecurities.sortedBy(SecurityAnalyzer::from).take(max(0, posSecurities.size - 1))

        callingBuySecurities = newSecurities.take(2 - posSecurities.size)
        val callingBuySlots = callingBuySecurities.size
        val callingBuyAmount = stage.cash
        callingBuySecurities.onEach {
            val amount = callingBuyAmount / callingBuySlots
            val price = buyPrice(it, barDate, informDate, readyDate) ?: 0.0
            val volume = (amount / price).floor(100.0)
            callingCommissions += Commission(Action.BUY, it, price, volume)
        }

        callingSellSecurities = sellSecurities
        callingSellSecurities.onEach {
            val close = it.indexDay["close", barDate].asDouble() ?: 0.0
            val price = close * 0.9
            callingCommissions += Commission(Action.SELL, it, price, stage.positions[it]?.available ?: 0.0)
        }
    }

    open fun isSafe(security: SecurityAnalyzer, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate): Boolean {
        return every(security.indexQuarter["profitForecast", informDate] gt 0.0, 12).asBool()
                && every(security.indexQuarter["profit", informDate].slice() gt 0.0, 12).asBool()
                && every(security.indexQuarter["profit", informDate].slice().let { it.previousYear() lt it }, 1).asBool()
                && every(security.indexQuarter["profitForecast", informDate].let { ref(it, 1) lt it }, 1).asBool()
                && security.indexQuarter["revenue", informDate].slice().let { it.previousYear() lt it }.asBool()
                && security.indexQuarter["revenueForecast", informDate].let { ref(it, 1) lt it }.asBool()
//                && (security.weekMacd(barDate, readyDate) gt 0.0).asBool()
    }

    open fun find(securities: List<SecurityAnalyzer>, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate): List<SecurityAnalyzer> {
        val filtered = securities.filter {
            isSafe(it, barDate, informDate, readyDate)
                    && it.indexDay.support(barDate).asDouble().let { s -> s !== null && s > 0.0 }
        }
        val support = filtered.map { it.indexDay.support(barDate).asDouble() }
        val close = filtered.map { it.indexDay["close", barDate].asDouble() }
        return filtered.indices.sortedWith(
                compareBy { let(close[it], support[it]) { c, s -> if (c > s) c / s else null } ?: Double.MAX_VALUE }
        ).map(filtered::get)
    }

    open fun buyPrice(security: SecurityAnalyzer, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate): Double? {
        val support = security.indexDay.support(barDate).asDouble()?.times(1.01)
        val supportDays = security.indexDay.supportDays(barDate).asInt() ?: 0
        logger.info("${security.id} $barDate 支撑在 ${security.tradingDates[security.tradingDates.indexOf(barDate) - supportDays]} $support")
        return support
    }

    fun SecurityIndexDayAnalyzer.support(barDate: LocalDate): List<SecurityIndexDay> {
        val open = this["open", barDate]
        val close = this["close", barDate]
        val low = this["low", barDate]
        val high = this["high", barDate]
        val volume = this["volume", barDate]
        val highr1 = ref(high, 1)
        val volume10 = ma(volume, 10)
        val volume10r1 = ref(volume10, 1)
        val jump = (highr1 lt low) and (open lt close) and (close eq high) and (volume gt (volume10r1 * 3.0))
        val offset = barslast(jump and (ref(jump, 1) eq 0.0))
        val bottom = ref(low, offset)
        val retained = every(low ge bottom, offset + 1.0)
        return bottom * retained
    }

    fun SecurityIndexDayAnalyzer.supportDays(barDate: LocalDate): List<SecurityIndexDay> {
        val open = this["open", barDate]
        val close = this["close", barDate]
        val low = this["low", barDate]
        val high = this["high", barDate]
        val volume = this["volume", barDate]
        val highr1 = ref(high, 1)
        val volume10 = ma(volume, 10)
        val volume10r1 = ref(volume10, 1)
        val jump = (highr1 lt low) and (open lt close) and (close eq high) and (volume gt (volume10r1 * 3.0))
        val offset = barslast(jump and (ref(jump, 1) eq 0.0))
        val bottom = ref(low, offset)
        val retained = every(low ge bottom, offset + 1.0)
        return offset * retained
    }
}