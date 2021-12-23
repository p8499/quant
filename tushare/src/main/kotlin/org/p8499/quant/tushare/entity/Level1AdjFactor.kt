package org.p8499.quant.tushare.entity

import java.io.Serializable
import java.time.LocalDate
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "F1503")
@IdClass(Level1AdjFactor.Level1AdjFactorId::class)
open class Level1AdjFactor(
        @Id
        @Column(nullable = false, length = 16)
        open var stockId: String? = null,

        @Id
        @Column(nullable = false, name = "dte")
//        @Temporal(TemporalType.DATE)
        open var date: LocalDate? = null,

        @Column(nullable = false, columnDefinition = "NUMBER(24, 12)", precision = 24, scale = 12)
        open var factor: Double? = null) {
    open class Level1AdjFactorId(
            open var stockId: String? = null,
            open var date: LocalDate? = null) : Serializable {
        override fun hashCode(): Int = Objects.hash(stockId, date)
        override fun equals(other: Any?): Boolean = other is Level1AdjFactorId && other.stockId == stockId && other.date == date
    }
}
