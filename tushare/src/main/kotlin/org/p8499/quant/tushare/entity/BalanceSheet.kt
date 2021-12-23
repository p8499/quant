package org.p8499.quant.tushare.entity

import java.io.Serializable
import java.time.LocalDate
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "F061")
@IdClass(BalanceSheet.BalanceSheetId::class)
open class BalanceSheet(
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
         * 股东权益合计(不含少数股东权益) -> 净资产
         */
        @Column(nullable = true, columnDefinition = "NUMBER(26, 2)", precision = 26, scale = 2)
        open var totalHldrEqyExcMinInt: Double? = null) {
    open class BalanceSheetId(
            open var stockId: String? = null,
            open var year: Int? = null,
            open var period: Int? = null) : Serializable {
        override fun hashCode(): Int = Objects.hash(stockId, year, period)
        override fun equals(other: Any?): Boolean = other is BalanceSheetId && other.stockId == stockId && other.year == year && other.period == period
    }
}
