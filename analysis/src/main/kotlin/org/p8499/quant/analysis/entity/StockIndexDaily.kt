package org.p8499.quant.analysis.entity

import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "SID")
@IdClass(StockIndexDaily.StockIndexDailyId::class)
data class StockIndexDaily(
        @Id
        @Column(nullable = false, length = 2)
        var region: String? = null,

        @Id
        @Column(nullable = false, length = 32)
        var id: String? = null,

        @Id
        @Column(nullable = false, length = 16)
        var kpi: String? = null,

        @Id
        @Column(nullable = false, name = "dte")
        @Temporal(TemporalType.DATE)
        var date: Date? = null,

        @Column(nullable = true, precision = 24, scale = 12)
        var value: Double? = null) {
    data class StockIndexDailyId(
            var region: String? = null,
            var id: String? = null,
            var kpi: String? = null,
            var date: Date? = null) : Serializable
}