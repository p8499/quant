package org.p8499.quant.tushare.entity

import java.io.Serializable
import java.time.LocalDate
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "F1502")
@IdClass(Level1Basic.Level1BasicId::class)
open class Level1Basic(
        @Id
        @Column(nullable = false, length = 16)
        open var stockId: String? = null,

        @Id
        @Column(nullable = false, name = "dte")
//        @Temporal(TemporalType.DATE)
        open var date: LocalDate? = null,

        @Column(nullable = false, columnDefinition = "NUMBER(18, 2)", precision = 18, scale = 2)
        open var totalShare: Double? = null,

        @Column(nullable = false, columnDefinition = "NUMBER(18, 2)", precision = 18, scale = 2)
        open var flowShare: Double? = null) {
    open class Level1BasicId(
            open var stockId: String? = null,
            open var date: LocalDate? = null) : Serializable {
        override fun hashCode(): Int = Objects.hash(stockId, date)
        override fun equals(other: Any?): Boolean = other is Level1BasicId && other.stockId == stockId && other.date == date
    }
}
