package org.p8499.quant.analysis.policy

import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.util.*

class Security(
        val region: String,
        val id: String,
        val limit: Int,
        val datesFunction: (String, String, Int) -> List<LocalDate>,
        val valuesFunction: (String, String, String, Int) -> List<Double?>,
        val messagesFunction: (String, String, Int) -> List<String?>,
        val quarterDatesFunction: (String, String, Int) -> List<LocalDate>,
        val quarterValuesFunction: (String, String, String, Int) -> List<Double?>,
        val quarterPublishesFunction: (String, String, String, Int) -> List<LocalDate?>) {
    protected val logger by lazy { LoggerFactory.getLogger(javaClass) }

    val date: List<LocalDate> by lazy { datesFunction(region, id, limit) }
    val open: List<Double?> get() = this["open"]
    val close: List<Double?> get() = this["close"]
    val high: List<Double?> get() = this["high"]
    val low: List<Double?> get() = this["low"]
    val volume: List<Double?> get() = this["volume"]
    val amount: List<Double?> get() = this["amount"]
    val totalShare: List<Double?> get() = this["totalShare"]
    val flowShare: List<Double?> get() = this["flowShare"]
    val totalValue: List<Double?> get() = this["totalValue"]
    val flowValue: List<Double?> get() = this["flowValue"]
    val pb: List<Double?> get() = this["pb"]
    val pe: List<Double?> get() = this["pe"]
    val ps: List<Double?> get() = this["ps"]
    val pcf: List<Double?> get() = this["pcf"]
    val message: List<String?> by lazy { messagesFunction(region, id, limit) }
    private val valuesMap: MutableMap<String, List<Double?>> = mutableMapOf()
    private val extension: MutableMap<String, () -> List<Double?>> = mutableMapOf()

    val quarterLimit = limit / 60
    val quarterDate: List<LocalDate> by lazy { quarterDatesFunction(region, id, limit) }
    fun asset(asOf: LocalDate): List<Double?> = this.getQuarter("asset", asOf)
    fun profit(asOf: LocalDate): List<Double?> = this.getQuarter("profit", asOf)
    fun revenue(asOf: LocalDate): List<Double?> = this.getQuarter("revenue", asOf)
    fun cashflow(asOf: LocalDate): List<Double?> = this.getQuarter("cashflow", asOf)
    private val quarterValuesMap: MutableMap<String, List<Double?>> = mutableMapOf()
    private val quarterPublishesMap: MutableMap<String, List<LocalDate?>> = mutableMapOf()

    operator fun get(kpi: String, asOf: LocalDate): Double? = dateIndex(asOf).takeIf { it > -1 }?.let { get(kpi)[it] }

    operator fun get(kpi: String): List<Double?> = safeGet(kpi) ?: throw NullPointerException()

    fun safeGet(kpi: String): List<Double?>? {
        return if (kpi in arrayOf("open", "close", "high", "low", "volume", "amount", "totalShare", "flowShare", "totalValue", "flowValue", "pb", "pe", "ps", "pcf"))
            if (kpi !in valuesMap.keys)
                valuesFunction(region, id, kpi, limit).also { valuesMap[kpi] = it }
            else
                valuesMap[kpi]
        else if (kpi in extension.keys)
            if (kpi !in valuesMap.keys)
                extension[kpi]?.invoke()?.also { valuesMap[kpi] = it }
            else
                valuesMap[kpi]
        else
            valuesMap[kpi]
    }

    fun register(kpi: String, transform: () -> List<Double?>) = extension.put(kpi, transform)

    fun message(asOf: LocalDate): String? = dateIndex(asOf).takeIf { it > -1 }?.let(message::get)

    private fun dateIndex(asOf: LocalDate): Int = date.indexOfLast { it <= asOf }


    fun getQuarter(kpi: String, date: LocalDate, asOf: LocalDate): Double? = dateIndexQuarter(kpi, asOf).takeIf { it > -1 }?.let { getQuarter(kpi, asOf)[it] }

    fun getQuarter(kpi: String, asOf: LocalDate): List<Double?> =
            safeGetQuarter(kpi, asOf) ?: throw NullPointerException()

    fun safeGetQuarter(kpi: String, asOf: LocalDate): List<Double?>? {
        //站在某一天拿到整个kpi列表
        val values = if (kpi in arrayOf("asset", "profit", "revenue", "cashflow") && kpi !in quarterValuesMap.keys) {
            quarterPublishesFunction(region, id, kpi, limit).also { quarterPublishesMap[kpi] = it }
            quarterValuesFunction(region, id, kpi, limit).also { quarterValuesMap[kpi] = it }
        } else
            quarterValuesMap[kpi]
        return values?.let { snapshotQuater(kpi, it, asOf) }
    }

    private fun dateIndexQuarter(kpi: String, asOf: LocalDate): Int =
            quarterPublishesMap[kpi]?.indexOfLast { it != null && it <= asOf } ?: -1

    private fun snapshotQuater(kpi: String, values: List<Double?>, asOf: LocalDate): List<Double?> = values.subList(0, dateIndexQuarter(kpi, asOf) + 1)

    override fun hashCode(): Int = Objects.hash(region, id)

    override fun equals(other: Any?): Boolean = other is Security && other.region == region && other.id == id
}



