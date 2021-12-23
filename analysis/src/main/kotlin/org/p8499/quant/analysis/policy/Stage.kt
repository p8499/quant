package org.p8499.quant.analysis.policy

import org.slf4j.LoggerFactory
import java.lang.Double.min
import java.time.LocalDate
import java.time.format.DateTimeFormatter


open class Stage(initCash: Double, val precision: Double) {
    protected val logger by lazy { LoggerFactory.getLogger(Stage::class.java) }

    protected var dateFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd")

    protected lateinit var tradingDates: List<LocalDate>

    protected lateinit var securityMap: Map<String, Security>

    var cash = initCash
        private set

    val positionMap: MutableMap<String, Pair<Security, Double>> = mutableMapOf()

    var date: LocalDate = LocalDate.ofEpochDay(0)
        private set

    val securities: List<Security> by lazy { securityMap.values.toList() }

    private fun position(region: String, id: String) = positionMap[encode(region, id)]?.second ?: 0.0

    fun positions(): List<Pair<Security, Double>> = positionMap.values.iterator().asSequence().toList()

    fun position(security: Security): Double = position(security.region, security.id)

    private fun encode(region: String, id: String) = "${region}-${id}"

    private fun decode(key: String) = key.substringBefore('-') to key.substringAfter('-')

    /**
     * buy at open
     * */
    fun buySlot(region: String, id: String, slot: Int) {
        buyAmount(region, id, cash / slot)
    }

    /**
     * buy at open
     * */
    fun buyAmount(region: String, id: String, amount: Double) {
        securityMap[encode(region, id)]?.let { security ->
            security.date.indexOf(date).takeIf { it > -1 }
                    ?.let(security.open::get)
                    ?.let { price -> buyVolume(region, id, (amount / price / precision).toInt() * precision) }
        }
    }

    /**
     * buy at open
     * */
    fun buyVolume(region: String, id: String, volume: Double) {
        securityMap[encode(region, id)]?.let { security ->
            security.date.indexOf(date).takeIf { it > -1 }
                    ?.let(security.open::get)
                    ?.let { price ->
                        var volumeAdjusted = volume
                        while (price * volumeAdjusted > cash) {
                            volumeAdjusted -= precision
                        }
                        if (volumeAdjusted > 0) {
                            val key = encode(security.region, security.id)
                            val available = (positionMap[key]?.second ?: 0.0)
                            positionMap[key] = security to available + volumeAdjusted
                            cash -= price * volumeAdjusted
                            logger.info("${dateFormat.format(date)} 多仓 ${key}，单价 $price，数量 $volumeAdjusted，现金余 $cash")
                        }
                    }
        }
    }

    /**
     * sell at open
     * */
    fun sell(region: String, id: String, volume: Double) {
        securityMap[encode(region, id)]?.let { security ->
            security.date.indexOf(date).takeIf { it > -1 }
                    ?.let(security.open::get)?.let { price ->
                        val key = encode(security.region, security.id)
                        val available = (positionMap[key]?.second ?: 0.0)
                        val volumeAdjusted = min(volume, available)
                        positionMap[key] = security to available - volumeAdjusted
                        key.takeIf { positionMap[key]?.second == 0.0 }.also(positionMap::remove)
                        cash += price * volumeAdjusted
                        logger.info("${dateFormat.format(date)} 平仓 ${key}，单价 $price，数量 $volumeAdjusted，现金余 $cash")
                    }
        }
    }

    /**
     * sell at open
     * */
    fun sellAll() {
        for (pair in positions()) {
            sell(pair.first.region, pair.first.id, pair.second)
        }
    }

    fun run(from: LocalDate, to: LocalDate, policy: Policy) {
        tradingDates = policy.dates()
        securityMap = mutableMapOf<String, Security>().apply {
            policy.select().onEach(policy::extend).forEach { put(encode(it.region, it.id), it) }
        }
        date = if (isTradingDate(from)) from else nextTradingDate(from, to)
        logger.info("初始价值 ${value(date)}")

        while (date <= to) {
            logger.info("${dateFormat.format(date)} 开始")
            policy.proceed(this, date)
            logger.info("${dateFormat.format(date)} 结束 持仓 ${positionMap.size} 价值 ${value(date)}")
            date = nextTradingDate(date, to)
        }
        logger.info("终末价值 ${value(date)}")
    }

    private fun isTradingDate(currentDate: LocalDate): Boolean {
        return tradingDates.indexOf(currentDate) > -1
    }

    private fun nextTradingDate(currentDate: LocalDate, finalDate: LocalDate): LocalDate {
        var nextDate = currentDate
        do {
            nextDate = nextDate.plusDays(1)
        } while (!isTradingDate(nextDate) && nextDate < finalDate)
        return nextDate
    }

    private fun value(currentDate: LocalDate): Double {
        return positions().sumOf {
            securityMap[encode(it.first.region, it.first.id)]?.let { security ->
                security.date.indexOf(date).takeIf { it > -1 }
                        ?.let(security.close::get)?.let { price -> price * it.second }
            } ?: 0.0
        } + cash
    }
}