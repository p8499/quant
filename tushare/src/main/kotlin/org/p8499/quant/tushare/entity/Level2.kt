package org.p8499.quant.tushare.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "F151", uniqueConstraints = [UniqueConstraint(columnNames = ["stockId", "dte"])])
data class Level2(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Level2_id")
        @SequenceGenerator(name = "Level2_id", allocationSize = 1)
        var id: Int? = null,

        @Column(nullable = false, length = 16)
        var stockId: String? = null,

        @Column(nullable = false, name = "dte")
        @Temporal(TemporalType.DATE)
        var date: Date? = null,

        @Column(nullable = false, precision = 18, scale = 2)
        var buy1: Double? = null,

        @Column(nullable = false, precision = 18, scale = 2)
        var sell1: Double? = null,

        @Column(nullable = false, precision = 18, scale = 2)
        var buy2: Double? = null,

        @Column(nullable = false, precision = 18, scale = 2)
        var sell2: Double? = null,

        @Column(nullable = false, precision = 18, scale = 2)
        var buy3: Double? = null,

        @Column(nullable = false, precision = 18, scale = 2)
        var sell3: Double? = null,

        @Column(nullable = false, precision = 18, scale = 2)
        var buy4: Double? = null,

        @Column(nullable = false, precision = 18, scale = 2)
        var sell4: Double? = null
)
