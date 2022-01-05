package org.p8499.quant.analysis.policy

import org.p8499.quant.analysis.common.let
import java.lang.Double.min
import java.time.LocalDate
import kotlin.math.ceil
import kotlin.math.floor

class Stage(val initCash: Double, val precision: Double) {
    private val initDate = LocalDate.ofEpochDay(0)

    private var date: LocalDate = initDate

    private var cash = initCash

    private val positions = mutableListOf<Position>()

    private val audit = Audit(this)

    fun date(): LocalDate = date

    fun cash(): Double = cash

    fun position(security: Security): Position? = positions.singleOrNull { it.security == security }

    fun positions(): List<Position> = positions

    fun log(content: String) = audit.log(content)

    fun log() = audit.toString()

    fun buySlot(security: Security, priceAs: String, slot: Int) =
            buyAmount(security, priceAs, value(priceAs) / slot)

    fun buyAmount(security: Security, priceAs: String, amount: Double) =
            security[priceAs, date]?.let { buyVolume(security, priceAs, amount / it) }

    fun buyVolume(security: Security, priceAs: String, volume: Double) {
        security[priceAs, date]?.let {
            val adjVolume = vf(min(cash / it, volume))
            if (adjVolume > 0) {
                cash -= it * adjVolume
                val position = position(security)
                if (position != null) {
                    position.cost = (position.cost * position.volume + it * adjVolume) / (position.volume + adjVolume)
                    position.volume += adjVolume
                } else
                    positions.add(Position(security, adjVolume, it))
                audit.buy(security, it, adjVolume)
            }
        }
    }

    fun sellSlot(security: Security, priceAs: String, slot: Int) =
            security[priceAs, date]?.let { sellAmount(security, priceAs, value(priceAs) / slot) }

    fun sellAmount(security: Security, priceAs: String, amount: Double) =
            security[priceAs, date]?.let { sellVolume(security, priceAs, amount / it) }

    fun sellVolume(security: Security, priceAs: String, volume: Double) {
        security[priceAs, date]?.let {
            val position = position(security)
            if (position != null) {
                val adjVolume = vc(min(volume, position.volume))
                position.volume -= adjVolume
                cash += it * adjVolume
                if (position.volume == 0.0)
                    positions.remove(position)
                audit.sell(position, it, adjVolume)
            }
        }
    }

    fun sell(security: Security, priceAs: String) {
        position(security)?.volume?.let { sellVolume(security, priceAs, it) }
    }

    fun sell(priceAs: String) {
        positions().forEach { sell(it.security, priceAs) }
    }

    fun adjustSlot(security: Security, priceAs: String, slot: Int) =
            security[priceAs, date]?.let { adjustAmount(security, priceAs, value(priceAs) / slot) }

    fun adjustAmount(security: Security, priceAs: String, amount: Double) =
            security[priceAs, date]?.let { adjustVolume(security, priceAs, amount / it) }

    fun adjustVolume(security: Security, priceAs: String, volume: Double) {
        val originalVolume = position(security)?.volume ?: 0.0
        if (originalVolume < volume)
            buyVolume(security, priceAs, volume - originalVolume)
        else if (volume < originalVolume)
            sellVolume(security, priceAs, originalVolume - volume)
    }

    private fun vf(volume: Double) = floor(volume / precision).toInt() * precision
    private fun vc(volume: Double) = ceil(volume / precision).toInt() * precision

    fun run(from: LocalDate, to: LocalDate, policy: Policy) {
        val tradingDates = policy.tradingDates(from, to)
        tradingDates.forEachIndexed { i, tradingDate ->
            date = tradingDate
            if (i == 0)
                policy.pre(this, date)
            policy.proceed(this, date)
            audit.snapshot()
            audit.wrap()
            if (i == tradingDates.size - 1)
                policy.post(this, date)
        }
    }

    fun reset() {
        date = initDate
        cash = initCash
        positions.clear()
        audit.clear()
    }

    fun value(priceAs: String): Double {
        return positions().sumOf { let(it.security[priceAs, date], it.volume) { a, b -> a * b } ?: 0.0 } + cash
    }

    class Position(val security: Security, var volume: Double, var cost: Double)
}