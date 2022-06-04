package org.p8499.quant.analysis.dayPolicy.cn

import org.p8499.quant.analysis.analyzer.SecurityAnalyzer
import org.p8499.quant.analysis.analyzer.SecurityIndexDayAnalyzer
import org.p8499.quant.analysis.analyzer.SecurityIndexQuarterAnalyzer
import org.p8499.quant.analysis.common.indexDay.*
import org.p8499.quant.analysis.common.messageQuarter.expire
import org.p8499.quant.analysis.entity.SecurityIndexDay
import org.p8499.quant.analysis.entity.SecurityMessageQuarter
import java.time.LocalDate

val SecurityIndexDayAnalyzer.open get() = get("open")
val SecurityIndexDayAnalyzer.close get() = get("close")
val SecurityIndexDayAnalyzer.high get() = get("high")
val SecurityIndexDayAnalyzer.low get() = get("low")
val SecurityIndexDayAnalyzer.volume get() = get("volume")
val SecurityIndexDayAnalyzer.amount get() = get("amount")
val SecurityIndexDayAnalyzer.flowShare get() = get("flowShare")
val SecurityIndexDayAnalyzer.flowValue get() = get("flowValue")
val SecurityIndexDayAnalyzer.totalShare get() = get("totalShare")
val SecurityIndexDayAnalyzer.totalValue get() = get("totalValue")
fun SecurityIndexQuarterAnalyzer.asset(asOf: LocalDate) = get("asset", asOf)
fun SecurityIndexQuarterAnalyzer.profit(asOf: LocalDate) = get("profit", asOf)
fun SecurityIndexQuarterAnalyzer.profitForecast(asOf: LocalDate) = get("profitForecast", asOf)
fun SecurityIndexQuarterAnalyzer.revenue(asOf: LocalDate) = get("revenue", asOf)
fun SecurityIndexQuarterAnalyzer.revenueForecast(asOf: LocalDate) = get("revenueForecast", asOf)
fun SecurityIndexQuarterAnalyzer.cash(asOf: LocalDate) = get("cash", asOf)
fun SecurityIndexQuarterAnalyzer.cashForecast(asOf: LocalDate) = get("cashForecast", asOf)

val SecurityIndexDayAnalyzer.mcst get() = get("mcst")
fun SecurityAnalyzer.mcst() = with(indexDay) {
    this["mcst"] = dma(amount / volume, volume / flowShare)
}

val SecurityIndexDayAnalyzer.turnover get() = get("turnover")
fun SecurityAnalyzer.turnover() = with(indexDay) {
    this["turnover"] = volume / flowShare * 100.0
}

val SecurityIndexDayAnalyzer.dif get() = get("dif")
val SecurityIndexDayAnalyzer.dea get() = get("dea")
val SecurityIndexDayAnalyzer.macd get() = get("macd")
fun SecurityAnalyzer.macd() = with(indexDay) {
    this["dif"] = ema(close, 12) - ema(close, 26)
    this["dea"] = ema(dif, 9)
    this["macd"] = (dif - dea) * 2.0
}

val SecurityIndexDayAnalyzer.rsv get() = get("rsv")
val SecurityIndexDayAnalyzer.k get() = get("k")
val SecurityIndexDayAnalyzer.d get() = get("d")
val SecurityIndexDayAnalyzer.j get() = get("j")
fun SecurityAnalyzer.kdj() = with(indexDay) {
    this["rsv"] = (close - llv(low, 9)) / (hhv(high, 9) - llv(low, 9)) * 100.0
    this["k"] = sma(rsv, 3, 1)
    this["d"] = sma(k, 3, 1)
    this["j"] = k * 3.0 - d * 3.0
}

//fun SecurityIndexDayAnalyzer.pressure(barDate: LocalDate): List<SecurityIndexDay> {
//    val low = this["low", barDate]
//    val high = this["high", barDate]
//    return ref(high, barslast(ref(low, 1) gt high))
//}

fun SecurityAnalyzer.pb(barDate: LocalDate, informDate: LocalDate) =
        indexDay["close", barDate] / (indexQuarter.flatten["asset", barDate, informDate] / indexDay["totalShare", barDate])

fun SecurityAnalyzer.pe(barDate: LocalDate, informDate: LocalDate) =
        indexDay["close", barDate] / (indexQuarter.flatten["profitForecast", barDate, informDate] / indexDay["totalShare", barDate])

fun SecurityAnalyzer.ps(barDate: LocalDate, informDate: LocalDate) =
        indexDay["close", barDate] / (indexQuarter.flatten["revenueForecast", barDate, informDate] / indexDay["totalShare", barDate])

fun SecurityAnalyzer.msg(informDate: LocalDate): List<SecurityMessageQuarter> =
        messageQuarter["message", informDate].apply { indexQuarter["profitForecast", informDate].lastOrNull()?.quarter?.let(this::expire) }

fun SecurityAnalyzer.weekDif(barDate: LocalDate, asOf: LocalDate): List<SecurityIndexDay> {
    val weekClose = indexDay["close", barDate].lastByWeek(asOf)
    return ema(weekClose, 12) - ema(weekClose, 26)
}

fun SecurityAnalyzer.weekDea(barDate: LocalDate, asOf: LocalDate): List<SecurityIndexDay> {
    val weekDif = weekDif(barDate, asOf)
    return ema(weekDif, 9)
}

fun SecurityAnalyzer.weekMacd(barDate: LocalDate, asOf: LocalDate): List<SecurityIndexDay> {
    val weekDif = weekDif(barDate, asOf)
    val weekDea = ema(weekDif, 9)
    return (weekDif - weekDea) * 2.0
}

fun SecurityAnalyzer.groupDif(barDate: LocalDate, groupSize: Int): List<SecurityIndexDay> {
    val groupClose = indexDay["close", barDate].lastBy(groupSize)
    return ema(groupClose, 12) - ema(groupClose, 26)
}

fun SecurityAnalyzer.groupDea(barDate: LocalDate, groupSize: Int): List<SecurityIndexDay> {
    val groupDif = groupDif(barDate, groupSize)
    return ema(groupDif, 9)
}

fun SecurityAnalyzer.groupMacd(barDate: LocalDate, groupSize: Int): List<SecurityIndexDay> {
    val groupDif = groupDif(barDate, groupSize)
    val groupDea = ema(groupDif, 9)
    return (groupDif - groupDea) * 2.0
}