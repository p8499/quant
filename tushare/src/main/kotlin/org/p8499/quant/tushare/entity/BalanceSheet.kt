package org.p8499.quant.tushare.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "F061", uniqueConstraints = [UniqueConstraint(columnNames = ["stockId", "year", "period"])])
data class BalanceSheet(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BalanceSheet_id")
        @SequenceGenerator(name = "BalanceSheet_id", allocationSize = 1)
        var id: Int? = null,

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
         * 股东权益合计(不含少数股东权益) -> 净资产
         */
        @Column(nullable = false, precision = 26, scale = 2)
        var totalHldrEqyExcMinInt: Double? = null
)
