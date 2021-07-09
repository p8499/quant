package org.p8499.quant.tushare.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "F06E", uniqueConstraints = [UniqueConstraint(columnNames = ["stockId", "year", "period"])])
data class Express(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Express_id")
        @SequenceGenerator(name = "Express_id", allocationSize = 1)
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
         * 股东权益合计(不含少数股东权益)(元) -> 净资产
         */
        @Column(nullable = false, precision = 26, scale = 2)
        var totalHldrEqyExcMinInt: Double? = null,

        /**
         * 营业收入(元) -> 营业收入(年初至今)
         */
        @Column(nullable = false, precision = 26, scale = 2)
        var revenue: Double? = null,
)
