package org.p8499.quant.analysis.entity

import java.io.Serializable
import java.time.LocalDate
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "SMD")
@IdClass(StockMessageDay.StockMessageDayId::class)
open class StockMessageDay(
        @Id
        @Column(nullable = false, length = 2)
        open var region: String? = null,

        @Id
        @Column(nullable = false, length = 32)
        open var id: String? = null,

        @Id
        @Column(nullable = false, name = "dte")
//        @Temporal(TemporalType.DATE)
        open var date: LocalDate? = null,

        @Lob
        @Column(nullable = true, columnDefinition = "CLOB")
        open var message: String? = null) {
    open class StockMessageDayId(
            open var region: String? = null,
            open var id: String? = null,
            open var date: LocalDate? = null) : Serializable {
        override fun hashCode(): Int = Objects.hash(region, id, date)
        override fun equals(other: Any?): Boolean = other is StockMessageDayId && other.region == region && other.date == date
    }
}