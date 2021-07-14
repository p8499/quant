package org.p8499.quant.tushare.entity

import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "F07")
@IdClass(Forecast.ForecastId::class)
data class Forecast(
        @Id
        @Column(nullable = false, length = 16)
        var stockId: String? = null,

        @Id
        @Column(nullable = false)
        var year: Short? = null,

        @Id
        @Column(nullable = false)
        var period: Short? = null,

        @Column(nullable = false)
        @Temporal(TemporalType.DATE)
        var publish: Date? = null,

        @Column(nullable = false, length = 64)
        var subject: String? = null,

        @Lob
        @Column(nullable = false, columnDefinition = "CLOB")
        var content: String? = null) {
    data class ForecastId(
            var stockId: String? = null,
            var year: Int? = null,
            var period: Int? = null) : Serializable
}
