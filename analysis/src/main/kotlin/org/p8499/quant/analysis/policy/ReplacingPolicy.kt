package org.p8499.quant.analysis.policy

import org.p8499.quant.analysis.common.gt
import org.p8499.quant.analysis.service.PolicyService
import java.time.LocalDate

abstract class ReplacingPolicy(val policyService: PolicyService) : Policy() {
    abstract fun find(securities: List<Security>, barDate: LocalDate): List<Security>

    abstract val slots: Int

    override fun proceed(stage: Stage, date: LocalDate) {
        val barDate = date.minusDays(1)
        var securitiesBuy = targets(stage, barDate, slots)
                .filter { tradable(it, date) }
        val securitiesSell = stage.positions()
                .map(Stage.Position::security)
                .filter { it !in securitiesBuy && !invincible(it, barDate) }
                .onEach { stage.sell(it, "open") }
        val freeSlots = { slots - stage.positions().size }
        val securitiesPos = stage.positions().map(Stage.Position::security)
        securitiesBuy = securitiesBuy
                .filter { it !in securitiesPos }
                .onEach { if (freeSlots() > 0) stage.buySlot(it, "open", freeSlots()) }
    }

    fun invincible(security: Security, barDate: LocalDate): Boolean = false

    fun tradable(security: Security, date: LocalDate): Boolean = (security["volume", date] gt 0.0) == 1.0

    fun targets(stage: Stage, barDate: LocalDate, size: Int): List<Security> = find(securities, barDate).let { if (it.size > size) it.subList(0, size) else it }
}