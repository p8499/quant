package org.p8499.quant.tushare.entity

import java.io.Serializable
import java.time.LocalDate
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "F1501")
@IdClass(Level1Candlestick.Level1CandlestickId::class)
open class Level1Candlestick(
        @Id
        @Column(nullable = false, length = 16)
        open var stockId: String? = null,

        @Id
        @Column(nullable = false, name = "dte")
//        @Temporal(TemporalType.DATE)
        open var date: LocalDate? = null,

        @Column(nullable = false, columnDefinition = "NUMBER(10, 2)", precision = 10, scale = 2)
        open var open: Double? = null,

        @Column(nullable = false, columnDefinition = "NUMBER(10, 2)", precision = 10, scale = 2)
        open var close: Double? = null,

        @Column(nullable = false, columnDefinition = "NUMBER(10, 2)", precision = 10, scale = 2)
        open var high: Double? = null,

        @Column(nullable = false, columnDefinition = "NUMBER(10, 2)", precision = 10, scale = 2)
        open var low: Double? = null,

        @Column(nullable = false, columnDefinition = "NUMBER(18, 2)", precision = 18, scale = 2)
        open var volume: Double? = null,

        @Column(nullable = false, columnDefinition = "NUMBER(26, 2)", precision = 26, scale = 2)
        open var amount: Double? = null) {
    open class Level1CandlestickId(
            open var stockId: String? = null,
            open var date: LocalDate? = null) : Serializable {
        override fun hashCode(): Int = Objects.hash(stockId, date)
        override fun equals(other: Any?): Boolean = other is Level1CandlestickId && other.stockId == stockId && other.date == date
    }
}
