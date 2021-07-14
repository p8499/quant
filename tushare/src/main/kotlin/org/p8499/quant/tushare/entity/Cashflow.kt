package org.p8499.quant.tushare.entity

import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "F063")
@IdClass(Cashflow.CashflowId::class)
data class Cashflow(
        @Id
        @Column(nullable = false, length = 16)
        var stockId: String? = null,

        @Id
        @Column(nullable = false)
        var year: Short? = null,

        @Id
        @Column(nullable = false)
        var period: Short? = null,

        @Column(nullable = false)
        @Temporal(TemporalType.DATE)
        var publish: Date? = null,

        /**
         * 经营活动产生的现金流量净额 -> 经营现金(年初至今)
         */
        @Column(nullable = false, precision = 26, scale = 2)
        var nCashflowAct: Double? = null) {
    data class CashflowId(
            var stockId: String? = null,
            var year: Int? = null,
            var period: Int? = null) : Serializable
}
