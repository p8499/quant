package org.p8499.quant.analysis.entity

import java.io.Serializable
import java.time.LocalDate
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "SID")
@IdClass(StockIndexDaily.StockIndexDailyId::class)
open class StockIndexDaily(
        @Id
        @Column(nullable = false, length = 2)
        open var region: String? = null,

        @Id
        @Column(nullable = false, length = 32)
        open var id: String? = null,

        @Id
        @Column(nullable = false, length = 16)
        open var kpi: String? = null,

        @Id
        @Column(nullable = false, name = "dte")
//        @Temporal(TemporalType.DATE)
        open var date: LocalDate? = null,

        @Column(nullable = true, columnDefinition = "NUMBER(32, 16)", precision = 32, scale = 16)
        open var value: Double? = null) {
    open class StockIndexDailyId(
            open var region: String? = null,
            open var id: String? = null,
            open var kpi: String? = null,
            open var date: LocalDate? = null) : Serializable {
        override fun hashCode(): Int = Objects.hash(region, id, kpi, date)
        override fun equals(other: Any?): Boolean = other is StockIndexDailyId && other.region == region && other.kpi == kpi && other.date == date
    }
}