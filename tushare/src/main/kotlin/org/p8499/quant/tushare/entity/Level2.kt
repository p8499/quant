package org.p8499.quant.tushare.entity

import java.io.Serializable
import java.time.LocalDate
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "F151")
@IdClass(Level2.Level2Id::class)
open class Level2(
        @Id
        @Column(nullable = false, length = 16)
        open var stockId: String? = null,

        @Id
        @Column(nullable = false, name = "dte")
//        @Temporal(TemporalType.DATE)
        open var date: LocalDate? = null,

        @Column(nullable = true, columnDefinition = "NUMBER(18, 2)", precision = 18, scale = 2)
        open var buy1: Double? = null,

        @Column(nullable = true, columnDefinition = "NUMBER(18, 2)", precision = 18, scale = 2)
        open var sell1: Double? = null,

        @Column(nullable = true, columnDefinition = "NUMBER(18, 2)", precision = 18, scale = 2)
        open var buy2: Double? = null,

        @Column(nullable = true, columnDefinition = "NUMBER(18, 2)", precision = 18, scale = 2)
        open var sell2: Double? = null,

        @Column(nullable = true, columnDefinition = "NUMBER(18, 2)", precision = 18, scale = 2)
        open var buy3: Double? = null,

        @Column(nullable = true, columnDefinition = "NUMBER(18, 2)", precision = 18, scale = 2)
        open var sell3: Double? = null,

        @Column(nullable = true, columnDefinition = "NUMBER(18, 2)", precision = 18, scale = 2)
        open var buy4: Double? = null,

        @Column(nullable = true, columnDefinition = "NUMBER(18, 2)", precision = 18, scale = 2)
        open var sell4: Double? = null) {
    open class Level2Id(
            open var stockId: String? = null,
            open var date: LocalDate? = null) : Serializable {
        override fun hashCode(): Int = Objects.hash(stockId, date)
        override fun equals(other: Any?): Boolean = other is Level2Id && other.stockId == stockId && other.date == date
    }
}

