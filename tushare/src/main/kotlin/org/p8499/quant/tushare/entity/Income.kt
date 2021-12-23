package org.p8499.quant.tushare.entity

import java.io.Serializable
import java.time.LocalDate
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "F062")
@IdClass(Income.IncomeId::class)
open class Income(
        @Id
        @Column(nullable = false, length = 16)
        open var stockId: String? = null,

        @Id
        @Column(nullable = false)
        open var year: Int? = null,

        @Id
        @Column(nullable = false)
        open var period: Int? = null,

        @Column(nullable = false)
//        @Temporal(TemporalType.DATE)
        open var publish: LocalDate? = null,

        /**
         * 营业收入 -> 营业收入(年初至今)
         */
        @Column(nullable = true, columnDefinition = "NUMBER(26, 2)", precision = 26, scale = 2)
        open var revenue: Double? = null,
        /**
         * 净利润(不含少数股东损益) -> 净利润(年初至今)
         */
        @Column(nullable = true, columnDefinition = "NUMBER(26, 2)", precision = 26, scale = 2)
        open var nIncomeAttrP: Double? = null) {
    open class IncomeId(
            open var stockId: String? = null,
            open var year: Int? = null,
            open var period: Int? = null) : Serializable {
        override fun hashCode(): Int = Objects.hash(stockId, year, period)
        override fun equals(other: Any?): Boolean = other is IncomeId && other.stockId == stockId && other.year == year && other.period == period
    }
}
