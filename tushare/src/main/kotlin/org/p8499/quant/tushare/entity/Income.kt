package org.p8499.quant.tushare.entity

import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "F062")
@IdClass(Income.IncomeId::class)
data class Income(
        @Id
        @Column(nullable = false, length = 16)
        var stockId: String? = null,

        @Id
        @Column(nullable = false)
        var year: Int? = null,

        @Id
        @Column(nullable = false)
        var period: Int? = null,

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
        var nIncomeAttrP: Double? = null) {
    data class IncomeId(
            var stockId: String? = null,
            var year: Int? = null,
            var period: Int? = null) : Serializable
}
