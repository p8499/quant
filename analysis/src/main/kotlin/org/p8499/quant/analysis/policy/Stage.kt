package org.p8499.quant.analysis.policy

import org.p8499.quant.analysis.common.let
import java.lang.Double.min
import java.time.LocalDate

open class Stage(val initCash: Double, val precision: Double) {
    private val initDate = LocalDate.ofEpochDay(0)

    private var date: LocalDate = initDate

    private var cash = initCash

    private val positions = mutableListOf<Position>()

    fun cash(): Double = cash

    fun position(security: Security): Position? = positions.singleOrNull { it.security == security }

    fun positions(): List<Position> = positions

    fun buySlot(security: Security, priceAs: String, slot: Int) {
        buyAmount(security, priceAs, cash / slot)
    }

    fun buyAmount(security: Security, priceAs: String, amount: Double) {
        security["open", date]
                ?.let { (amount / it / precision).toInt() * precision }
                ?.let { buyVolume(security, priceAs, it) }
    }

    fun buyVolume(security: Security, priceAs: String, volume: Double) {
        val adjust: (Double) -> Double = { price ->
            var adjVolume = volume
            while (price * adjVolume > cash) {
                adjVolume -= precision
            }
            adjVolume
        }
        security[priceAs, date]?.let {
            val adjVolume = adjust(it)
            if (adjVolume > 0) {
                cash -= it * adjVolume
                val position = position(security)
                if (position != null) {
                    position.cost = (position.cost * position.volume + it * adjVolume) / (position.volume + adjVolume)
                    position.volume += adjVolume
                } else
                    positions.add(Position(security, adjVolume, it))
            }
        }
    }

    fun sell(security: Security, priceAs: String, volume: Double) {
        security[priceAs, date]?.let {
            val position = position(security)
            if (position != null) {
                val adjVolume = min(volume, position.volume)
                position.volume -= adjVolume
                cash += it * adjVolume
                if (position.volume == 0.0)
                    positions.remove(position)
            }
        }
    }

    fun sell(security: Security, priceAs: String) {
        position(security)?.volume?.let { sell(security, priceAs, it) }
    }

    fun sell(priceAs: String) {
        positions().forEach { sell(it.security, priceAs) }
    }

    fun run(from: LocalDate, to: LocalDate, policy: Policy) {
        val tradingDates = policy.tradingDates(from, to)
        tradingDates.forEachIndexed { i, tradingDate ->
            date = tradingDate
            if (i == 0)
                policy.pre(this, date)
            policy.proceed(this, date)
            if (i == tradingDates.size - 1)
                policy.post(this, date)
        }
    }

    fun reset() {
        date = initDate
        cash = initCash
        positions.clear()
    }

    fun value(): Double {
        return positions().sumOf { let(it.security["close", date], it.volume) { a, b -> a * b } ?: 0.0 } + cash
    }

    class Position(val security: Security, var volume: Double, var cost: Double)
}