package org.p8499.quant.tushare.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "F07", uniqueConstraints = [UniqueConstraint(columnNames = ["stockId", "year", "period"])])
data class Forecast(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Forecast_id")
        @SequenceGenerator(name = "Forecast_id", allocationSize = 1)
        var id: Long? = null,

        @Column(nullable = false, length = 16)
        var stockId: String? = null,

        @Column(nullable = false)
        var year: Short? = null,

        @Column(nullable = false)
        var period: Short? = null,

        @Column(nullable = false)
        @Temporal(TemporalType.DATE)
        var publish: Date? = null,

        @Column(nullable = false, length = 64)
        var subject: String? = null,

        @Lob
        @Column(nullable = false, columnDefinition = "CLOB")
        var content: String? = null,
)
