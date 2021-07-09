package org.p8499.quant.tushare.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "F062", uniqueConstraints = [UniqueConstraint(columnNames = ["stockId", "year", "period"])])
data class Income(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Income_id")
        @SequenceGenerator(name = "Income_id", allocationSize = 1)
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
         * 营业收入 -> 营业收入(年初至今)
         */
        @Column(nullable = false, precision = 26, scale = 2)
        var revenue: Double? = null,
        /**
         * 净利润(不含少数股东损益) -> 净利润(年初至今)
         */
        @Column(nullable = false, precision = 26, scale = 2)
        var nIncomeAttrP: Double? = null
)
