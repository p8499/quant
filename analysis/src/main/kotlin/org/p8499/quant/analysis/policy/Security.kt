package org.p8499.quant.analysis.policy

import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.util.*

class Security(
        val region: String,
        val id: String,
        val limit: Int,
        val sizeFunction: (String, String) -> Int,
        val datesFunction: (String, String, Int) -> List<LocalDate>,
        val valuesFunction: (String, String, String, Int) -> List<Double?>,
        val messagesFunction: (String, String, Int) -> List<String?>) {
    protected val logger by lazy { LoggerFactory.getLogger(Stage::class.java) }
    val size: Int by lazy { sizeFunction(region, id) }
    val date: List<LocalDate> by lazy { datesFunction(region, id, limit) }
    val open: List<Double?> get() = this["open"] as List<Double?>
    val close: List<Double?> get() = this["close"] as List<Double?>
    val high: List<Double?> get() = this["high"] as List<Double?>
    val low: List<Double?> get() = this["low"] as List<Double?>
    val volume: List<Double?> get() = this["volume"] as List<Double?>
    val amount: List<Double?> get() = this["amount"] as List<Double?>
    val totalShare: List<Double?> get() = this["totalShare"] as List<Double?>
    val flowShare: List<Double?> get() = this["flowShare"] as List<Double?>
    val totalValue: List<Double?> get() = this["totalValue"] as List<Double?>
    val flowValue: List<Double?> get() = this["flowValue"] as List<Double?>
    val asset: List<Double?> get() = this["asset"] as List<Double?>
    val profit: List<Double?> get() = this["profit"] as List<Double?>
    val revenue: List<Double?> get() = this["revenue"] as List<Double?>
    val cashflow: List<Double?> get() = this["cashflow"] as List<Double?>
    val pb: List<Double?> get() = this["pb"] as List<Double?>
    val pe: List<Double?> get() = this["pe"] as List<Double?>
    val ps: List<Double?> get() = this["ps"] as List<Double?>
    val pcf: List<Double?> get() = this["pcf"] as List<Double?>
    val message: List<String?> by lazy { messagesFunction(region, id, limit) }

    operator fun get(kpi: String): List<Double?>? {
        return if (arrayOf("open", "close", "high", "low", "volume", "amount", "totalShare", "flowShare", "totalValue", "flowValue", "asset", "profit", "revenue", "cashflow", "pb", "pe", "ps", "pcf").contains(kpi))
            if (!valuesMap.containsKey(kpi))
                valuesFunction(region, id, kpi, limit).also { valuesMap[kpi] = it }
            else
                valuesMap[kpi]
        else if (kpi in extension.keys)
            if (!valuesMap.containsKey(kpi))
                extension[kpi]?.invoke()?.also { valuesMap[kpi] = it }
            else
                valuesMap[kpi]
        else
            valuesMap[kpi]
    }

    private fun dateIndex(asOf: LocalDate): Int = date.indexOfLast { it <= asOf }

    operator fun get(kpi: String, asOf: LocalDate): Double? = dateIndex(asOf).takeIf { it > -1 }?.let { get(kpi)?.get(it) }

    private val valuesMap: MutableMap<String, List<Double?>> = mutableMapOf()

    private val extension: MutableMap<String, () -> List<Double?>> = mutableMapOf()

    fun message(asOf: LocalDate): String? = dateIndex(asOf).takeIf { it > -1 }?.let(message::get)

    fun register(kpi: String, transform: () -> List<Double?>) = extension.put(kpi, transform)

    override fun hashCode(): Int = Objects.hash(region, id)

    override fun equals(other: Any?): Boolean = other is Security && other.region == region && other.id == id
}



