package org.p8499.quant.tushare.entity

import java.io.Serializable
import java.time.LocalDate
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "F02")
@IdClass(TradingDate.TradingDateId::class)
open class TradingDate(
        @Id
        @Column(nullable = false, length = 4)
        open var exchangeId: String? = null,

        @Id
        @Column(nullable = false, name = "dte")
//        @Temporal(TemporalType.DATE)
        open var date: LocalDate? = null) {
    open class TradingDateId(
            open var exchangeId: String? = null,
            open var date: LocalDate? = null) : Serializable {
        override fun hashCode(): Int = Objects.hash(exchangeId, date)
        override fun equals(other: Any?): Boolean = other is TradingDateId && other.exchangeId == exchangeId && other.date == date
    }
}
