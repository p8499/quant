package org.p8499.quant.analysis.policy

import org.p8499.quant.analysis.common.gt
import org.p8499.quant.analysis.service.PolicyService
import java.time.LocalDate
import kotlin.math.min

abstract class PartitionedPolicy(val policyService: PolicyService) : Policy() {
    abstract fun find(securities: List<Security>, barDate: LocalDate): List<Security>
    abstract val slots: Int
    override fun proceed(stage: Stage, date: LocalDate) {
        val barDate = date.minusDays(1)
        val securitiesPos = stage.positions().map(Stage.Position::security)
        val securitiesTarget = targets(stage, barDate, slots)
                .filter { tradable(it, date) }
                .let { it.subList(0, min(slots, it.size)) }
        val securitiesSell = securitiesPos.filter { it !in securitiesTarget }.onEach { stage.sell(it, "open") }
        val securitiesAdjust = securitiesPos.filter { it in securitiesTarget }.onEach { stage.adjustSlot(it, "open", securitiesTarget.size) }
        val securitiesBuy = securitiesTarget.filter { it !in securitiesPos }.onEach { stage.buySlot(it, "open", securitiesTarget.size) }
    }

    fun invincible(security: Security, barDate: LocalDate): Boolean = false

    fun tradable(security: Security, date: LocalDate): Boolean = (security["volume", date] gt 0.0) == 1.0

    fun targets(stage: Stage, barDate: LocalDate, size: Int): List<Security> = find(securities, barDate).let { if (it.size > size) it.subList(0, size) else it }
}