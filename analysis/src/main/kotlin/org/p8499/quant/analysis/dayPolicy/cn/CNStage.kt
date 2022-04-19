package org.p8499.quant.analysis.dayPolicy.cn

import org.p8499.quant.analysis.analyzer.SecurityAnalyzer
import org.p8499.quant.analysis.common.ceil
import org.p8499.quant.analysis.common.indexDay.asBool
import org.p8499.quant.analysis.common.indexDay.asDouble
import org.p8499.quant.analysis.common.indexDay.gt
import org.p8499.quant.analysis.common.round
import org.p8499.quant.analysis.dayPolicy.*
import org.p8499.quant.analysis.dayPolicy.common.get
import org.p8499.quant.analysis.dayPolicy.common.price
import org.p8499.quant.analysis.dayPolicy.common.value
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.locks.ReentrantLock
import kotlin.math.max
import kotlin.math.min

class CNStage : Stage<CNStatus> {
    val lock = ReentrantLock()

    override var date: LocalDate = LocalDate.ofEpochDay(0)
    override var status: CNStatus = CNStatus.BEFORE

    override var cash: Double = 0.0
    override val positions = mutableListOf<Position>()
    override val commissions = mutableListOf<Commission>()
    override val transactions = mutableListOf<Transaction>()
    override val snapshots = mutableListOf<Snapshot<CNStatus>>()

    override fun run(toDate: LocalDate, toStatus: CNStatus, policy: Policy<CNStatus>) {
        while (date < toDate || date == toDate && status < toStatus)
            promote(policy)
    }

    private fun promote(policy: Policy<CNStatus>) {
        lock.lock()
        when {
            status == CNStatus.BEFORE && policy.isTradingDate(date) -> {
                // 09:15
                status = CNStatus.CALLING
            }
            status == CNStatus.CALLING && policy.isTradingDate(date) -> {
                // 09:25
                status = CNStatus.OPENING
                callingTrade()
                policy.proceed(this)
                policy.openingCommissions.forEach(::appendCommission)
            }
            status == CNStatus.OPENING && policy.isTradingDate(date) -> {
                // 09:30
                status = CNStatus.TRADING
                openingTrade()//trade use open
            }
            status == CNStatus.TRADING && policy.isTradingDate(date) -> {
                // 14:57
                status = CNStatus.CLOSING
                tradingTrade()//trade use close
            }
            status == CNStatus.CLOSING && policy.isTradingDate(date) -> {
                // 15:00
                status = CNStatus.AFTER
                roll()
                saveSnapshot()
            }
            status == CNStatus.AFTER && policy.isTradingDate(date) -> {
                // 00:00
                date = date.plusDays(1)
                status = CNStatus.BEFORE
                policy.callingCommissions.clear()
                policy.openingCommissions.clear()
                policy.proceed(this)
                policy.callingCommissions.forEach(::appendCommission)
            }
            else -> {
                // 00:00 of None Trading Date
                date = date.plusDays(1)
                status = CNStatus.BEFORE
                policy.callingCommissions.clear()
                policy.openingCommissions.clear()
                policy.proceed(this)
            }
        }
        lock.unlock()
    }

    override fun value(): Double = convert(date, status).let { value(it.first, it.second) }

    override fun positionValue(security: SecurityAnalyzer) = when (status) {
        CNStatus.BEFORE, CNStatus.CALLING -> positions[security]?.value(date.minusDays(1), "close") ?: 0.0
        CNStatus.OPENING, CNStatus.TRADING -> positions[security]?.value(date, "open") ?: 0.0
        CNStatus.CLOSING, CNStatus.AFTER -> positions[security]?.value(date, "close") ?: 0.0
    }

    private fun appendCommission(commission: Commission) {
//        val price = if (commission.action == Action.BUY) commission.price.ceil(0.01) else commission.price.floor(0.01)
        val volume = commission.volume.ceil(100.0)
        if ((commission.security.indexDay["volume", date] gt 0.0).asBool() && volume > 0)
            if (commission.action == Action.BUY) {
                val amount = commission.price * volume
                if (cash >= amount) {
                    cash -= amount
                    commissions += Commission(commission.action, commission.security, commission.price, volume)
                }
            } else {
                val position = positions[commission.security]
                if (position?.available?.let { it >= volume } == true) {
                    position.available -= volume
                    position.unavailable += volume
                    commissions += Commission(commission.action, commission.security, commission.price, volume)
                }
            }
    }

    private fun internalTrade(dateTime: LocalDateTime, commission: Commission, price: Double) {
        if (commission.action == Action.BUY) {
            // add position
            val position = positions[commission.security]
            if (position != null) {
                position.cost = (position.cost * position.volume + price * commission.volume) / (position.volume + commission.volume)
                position.unavailable += commission.volume
            } else
                positions.add(Position(commission.security, 0.0, commission.volume, price))
            // add cash
            cash += (commission.price - price) * commission.volume
            transactions += Transaction(dateTime, commission.action, commission.security, price.round(0.01), commission.volume, null, null)
        } else {
            // remove position
            val position = positions[commission.security]
            if (position != null) {
                position.available -= commission.volume
                if (position.volume == 0.0)
                    positions.remove(position)
            }
            // add cash
            cash += price * commission.volume
            transactions += Transaction(dateTime, commission.action, commission.security, price.round(0.01), commission.volume, position?.let { (price - it.cost) * commission.volume }, position?.let { (price - it.cost) / it.cost })
        }
    }

    //trade use open-optimistic
    private fun callingTrade() {
        val iterator = commissions.iterator()
        while (iterator.hasNext()) {
            val commission = iterator.next()
            commission.security.indexDay["open", date].asDouble()?.takeIf {
                if (commission.action == Action.BUY) it <= commission.price else it >= commission.price
            }?.let {
                iterator.remove()
                internalTrade(LocalDateTime.of(date, LocalTime.of(9, 25)), commission, it)
            }
        }
    }

    //trade use open-pessimistic
    private fun openingTrade() {
        val iterator = commissions.iterator()
        while (iterator.hasNext()) {
            val commission = iterator.next()
            commission.security.indexDay["open", date].asDouble()?.takeIf {
                if (commission.action == Action.BUY) it < commission.price else it > commission.price
            }?.let {
                iterator.remove()
                internalTrade(LocalDateTime.of(date, LocalTime.of(9, 30)), commission, it)
            }
        }
    }

    //trade use commission price within high/low
    private fun tradingTrade() {
        val iterator = commissions.iterator()
        while (iterator.hasNext()) {
            val commission = iterator.next()
            (if (commission.action == Action.BUY) commission.security.indexDay["open", date].asDouble()?.let { min(it, commission.price) }
            else commission.security.indexDay["open", date].asDouble()?.let { max(it, commission.price) })?.takeIf { price ->
                if (commission.action == Action.BUY) commission.security.indexDay["low", date].asDouble()?.let { it < price } == true
                else commission.security.indexDay["high", date].asDouble()?.let { it > price } == true
            }?.let {
                iterator.remove()
                internalTrade(LocalDateTime.of(date, LocalTime.of(14, 57)), commission, it)
            }
        }
    }

    private fun roll() {
        // rollback commission
        val iterator = commissions.iterator()
        while (iterator.hasNext()) {
            val commission = iterator.next()
            iterator.remove()
            if (commission.action == Action.BUY) {
                cash += commission.price * commission.volume
            } else {
                val position = positions[commission.security]
                if (position != null) {
                    position.unavailable -= commission.volume
                    position.available += commission.volume
                }
            }
        }
        // move position to available
        for (position in positions) {
            position.available += position.unavailable
            position.unavailable = 0.0
        }
    }

    private fun saveSnapshot() {
        val pair = convert(date, status)
        snapshots += Snapshot(date, status, cash, positions.map { SnapshotPosition(it.security.id, it.cost, it.price(pair.first, pair.second) ?: 0.0, it.volume) })
    }
}
