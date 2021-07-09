package org.p8499.quant.tushare.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "F150", uniqueConstraints = [UniqueConstraint(columnNames = ["stockId", "dte"])])
data class Level1(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Level1_id")
        @SequenceGenerator(name = "Level1_id", allocationSize = 1)
        var id: Int? = null,

        @Column(nullable = false, length = 16)
        var stockId: String? = null,

        @Column(nullable = false, name = "dte")
        @Temporal(TemporalType.DATE)
        var date: Date? = null,

        @Column(nullable = false, precision = 10, scale = 2)
        var open: Double? = null,

        @Column(nullable = false, precision = 10, scale = 2)
        var close: Double? = null,

        @Column(nullable = false, precision = 10, scale = 2)
        var high: Double? = null,

        @Column(nullable = false, precision = 10, scale = 2)
        var low: Double? = null,

        @Column(nullable = false, precision = 18, scale = 2)
        var volume: Double? = null,

        @Column(nullable = false, precision = 26, scale = 2)
        var amount: Double? = null,

        @Column(nullable = false, precision = 18, scale = 2)
        var totalShare: Double? = null,

        @Column(nullable = false, precision = 18, scale = 2)
        var flowShare: Double? = null,

        @Column(nullable = false, precision = 24, scale = 12)
        var factor: Double? = null
)
