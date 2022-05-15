package org.p8499.quant.analysis.dayPolicy.cn.policy20

import org.p8499.quant.analysis.analyzer.RegionAnalyzer
import org.p8499.quant.analysis.analyzer.SecurityAnalyzer
import org.p8499.quant.analysis.common.floor
import org.p8499.quant.analysis.common.indexDay.asDouble
import org.p8499.quant.analysis.common.indexDay.div
import org.p8499.quant.analysis.common.indexDay.ma
import org.p8499.quant.analysis.common.indexQuarter.*
import org.p8499.quant.analysis.dayPolicy.Action
import org.p8499.quant.analysis.dayPolicy.Commission
import org.p8499.quant.analysis.dayPolicy.Position
import org.p8499.quant.analysis.dayPolicy.Stage
import org.p8499.quant.analysis.dayPolicy.cn.CNPolicy
import org.p8499.quant.analysis.dayPolicy.cn.CNStatus
import org.p8499.quant.analysis.dayPolicy.cn.close
import org.p8499.quant.analysis.dayPolicy.common.get
import java.time.LocalDate

//不做调整，设定卖出
public open class Policy20(regionAnalyzer: RegionAnalyzer) : CNPolicy(regionAnalyzer) {
    open val securities = regionAnalyzer.securities { true }.onEach {
//        it.indexDay["close30"] = ma(it.indexDay.close, 30)
    }

    open val slots = 1

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

    override fun hintForOpening(stage: Stage<CNStatus>, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate): String {
        return ""
//        return "最多${slots}个仓位\n" + (newSecurities - callingBuySecurities).take(slots).joinToString("\n") { "BUY\t${it.id}\t${it.indexDay["close", barDate].asDouble()?.let { close -> close * 1.05 } ?: 0.0}" }
    }

    override fun onOpening(stage: Stage<CNStatus>, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate) {
//        val posSecurities = stage.positions.map(Position::security)
//        openingBuySecurities = (newSecurities - callingBuySecurities).take(slots - posSecurities.size - stage.commissions.filter { it.action == Action.BUY }.size)
//        val openingBuySlots = openingBuySecurities.size
//        val openingBuyAmount = stage.cash
//        openingBuySecurities.onEach {
//            val close = it.indexDay["close", barDate].asDouble() ?: 0.0
//            val amount = openingBuyAmount / openingBuySlots
//            val price = close * 1.05
//            val volume = (amount / price).floor(100.0)
//            openingCommissions += Commission(Action.BUY, it, price, volume)
//        }
    }

    open fun isSafe(security: SecurityAnalyzer, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate): Boolean {
        return every(security.indexQuarter["profitForecast", informDate] gt 0.0, 12).asBool()
                && every(security.indexQuarter["profit", informDate].slice() gt 0.0, 12).asBool()
                && every(security.indexQuarter["profit", informDate].slice().let { it.previousYear() lt it }, 4).asBool()
                && every(security.indexQuarter["profitForecast", informDate].let { ref(it, 1) lt it }, 4).asBool()
                && security.indexQuarter["revenue", informDate].slice().let { it.previousYear() lt it }.asBool()
                && security.indexQuarter["revenueForecast", informDate].let { ref(it, 1) lt it }.asBool()
    }

    open fun find(securities: List<SecurityAnalyzer>, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate): List<SecurityAnalyzer> {
        val filtered = securities.filter { isSafe(it, barDate, informDate, readyDate) }
        return filtered.sortedWith(compareBy(
                { (it.indexDay["close", barDate] / it.indexDay["close30", barDate]).asDouble() ?: Double.MAX_VALUE }))
    }

    open fun sellPrice(security: SecurityAnalyzer, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate): Double? {
        return security.indexDay["close30", barDate].asDouble()
    }

    open fun buyPrice(security: SecurityAnalyzer, barDate: LocalDate, informDate: LocalDate, readyDate: LocalDate): Double? {
        return security.indexDay["close30", barDate].asDouble()
    }
}