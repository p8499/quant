package org.p8499.quant.tushare.entity

import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "F151")
@IdClass(Level2.Level2Id::class)
data class Level2(
        @Id
        @Column(nullable = false, length = 16)
        var stockId: String? = null,

        @Id
        @Column(nullable = false, name = "dte")
        @Temporal(TemporalType.DATE)
        var date: Date? = null,

        @Column(nullable = true, columnDefinition = "NUMBER(18, 2)", precision = 18, scale = 2)
        var buy1: Double? = null,

        @Column(nullable = true, columnDefinition = "NUMBER(18, 2)", precision = 18, scale = 2)
        var sell1: Double? = null,

        @Column(nullable = true, columnDefinition = "NUMBER(18, 2)", precision = 18, scale = 2)
        var buy2: Double? = null,

        @Column(nullable = true, columnDefinition = "NUMBER(18, 2)", precision = 18, scale = 2)
        var sell2: Double? = null,

        @Column(nullable = true, columnDefinition = "NUMBER(18, 2)", precision = 18, scale = 2)
        var buy3: Double? = null,

        @Column(nullable = true, columnDefinition = "NUMBER(18, 2)", precision = 18, scale = 2)
        var sell3: Double? = null,

        @Column(nullable = true, columnDefinition = "NUMBER(18, 2)", precision = 18, scale = 2)
        var buy4: Double? = null,

        @Column(nullable = true, columnDefinition = "NUMBER(18, 2)", precision = 18, scale = 2)
        var sell4: Double? = null) {
    data class Level2Id(
            var stockId: String? = null,
            var date: Date? = null) : Serializable
}

