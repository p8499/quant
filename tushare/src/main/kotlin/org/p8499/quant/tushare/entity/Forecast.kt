package org.p8499.quant.tushare.entity

import java.io.Serializable
import java.time.LocalDate
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "F07")
@IdClass(Forecast.ForecastId::class)
open class Forecast(
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

        @Column(nullable = false, length = 64)
        open var subject: String? = null,

        @Lob
        @Column(nullable = true, columnDefinition = "CLOB")
        open var content: String? = null) {
    open class ForecastId(
            open var stockId: String? = null,
            open var year: Int? = null,
            open var period: Int? = null) : Serializable {
        override fun hashCode(): Int = Objects.hash(stockId, year, period)
        override fun equals(other: Any?): Boolean = other is ForecastId && other.stockId == stockId && other.year == year && other.period == period
    }
}
