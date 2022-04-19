package org.p8499.quant.tushare.entity

import java.io.Serializable
import java.time.LocalDate
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "F063")
@IdClass(Cashflow.CashflowId::class)
open class Cashflow(
        @Id
        @Column(nullable = false, length = 16)
        open var stockId: String? = null,

        @Id
        @Column(nullable = false)
        open var publish: LocalDate? = null,

        @Id
        @Column(nullable = false)
        open var year: Int? = null,

        @Id
        @Column(nullable = false)
        open var period: Int? = null,

        /**
         * 经营活动产生的现金流量净额 -> 经营现金(年初至今)
         */
        @Column(nullable = true, columnDefinition = "NUMBER(26, 2)", precision = 26, scale = 2)
        open var nCashflowAct: Double? = null) {
    open class CashflowId(
            open var stockId: String? = null,
            open var publish: LocalDate? = null,
            open var year: Int? = null,
            open var period: Int? = null) : Serializable {
        override fun hashCode(): Int = Objects.hash(stockId, publish, year, period)
        override fun equals(other: Any?): Boolean = other is CashflowId && other.stockId == stockId && other.publish == publish && other.year == year && other.period == period
    }
}