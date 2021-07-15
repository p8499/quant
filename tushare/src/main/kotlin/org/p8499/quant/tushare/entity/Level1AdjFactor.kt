package org.p8499.quant.tushare.entity

import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "F1503")
@IdClass(Level1AdjFactor.Level1AdjFactorId::class)
data class Level1AdjFactor(
        @Id
        @Column(nullable = false, length = 16)
        var stockId: String? = null,

        @Id
        @Column(nullable = false, name = "dte")
        @Temporal(TemporalType.DATE)
        var date: Date? = null,

        @Column(nullable = false, precision = 24, scale = 12)
        var factor: Double? = null) {
    data class Level1AdjFactorId(
            var stockId: String? = null,
            var date: Date? = null) : Serializable
}
