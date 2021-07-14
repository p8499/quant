package org.p8499.quant.tushare.entity

import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "F02")
@IdClass(TradingDate.TradingDateId::class)
data class TradingDate(
        @Id
        @Column(nullable = false, length = 4)
        var exchangeId: String? = null,

        @Id
        @Column(nullable = false, name = "dte")
        @Temporal(TemporalType.DATE)
        var date: Date? = null) {
    data class TradingDateId(
            var exchangeId: String? = null,
            var date: Date? = null) : Serializable
}
