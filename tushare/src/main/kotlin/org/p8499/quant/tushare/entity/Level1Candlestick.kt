package org.p8499.quant.tushare.entity

import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "F1501")
@IdClass(Level1Candlestick.Level1CandlestickId::class)
data class Level1Candlestick(
        @Id
        @Column(nullable = false, length = 16)
        var stockId: String? = null,

        @Id
        @Column(nullable = false, name = "dte")
        @Temporal(TemporalType.DATE)
        var date: Date? = null,

        @Column(nullable = false, precision = 10, scale = 2)
        var open: Double? = null,

        @Column(nullable = false, precision = 10, scale = 2)
        var close: Double? = null,

        @Column(nullable = false, precision = 10, scale = 2)
        var high: Double? = null,

        @Column(nullable = false, precision = 10, scale = 2)
        var low: Double? = null,

        @Column(nullable = false, precision = 18, scale = 2)
        var volume: Double? = null,

        @Column(nullable = false, precision = 26, scale = 2)
        var amount: Double? = null) {
    data class Level1CandlestickId(
            var stockId: String? = null,
            var date: Date? = null) : Serializable
}
