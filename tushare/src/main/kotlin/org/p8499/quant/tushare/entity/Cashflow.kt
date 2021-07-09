package org.p8499.quant.tushare.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "F063", uniqueConstraints = [UniqueConstraint(columnNames = ["stockId", "year", "period"])])
data class Cashflow(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Cashflow_id")
        @SequenceGenerator(name = "Cashflow_id", allocationSize = 1)
        var id: Long? = null,

        @Column(nullable = false, length = 16)
        var stockId: String? = null,

        @Column(nullable = false)
        var year: Short? = null,

        @Column(nullable = false)
        var period: Short? = null,

        @Column(nullable = false)
        @Temporal(TemporalType.DATE)
        var publish: Date? = null,

        /**
         * 经营活动产生的现金流量净额 -> 经营现金(年初至今)
         */
        @Column(nullable = false, precision = 26, scale = 2)
        var nCashflowAct: Double? = null,
)
