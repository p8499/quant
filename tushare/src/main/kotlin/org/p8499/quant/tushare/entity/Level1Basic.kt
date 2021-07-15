package org.p8499.quant.tushare.entity

import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "F1502")
@IdClass(Level1Basic.Level1BasicId::class)
data class Level1Basic(
        @Id
        @Column(nullable = false, length = 16)
        var stockId: String? = null,

        @Id
        @Column(nullable = false, name = "dte")
        @Temporal(TemporalType.DATE)
        var date: Date? = null,

        @Column(nullable = false, precision = 18, scale = 2)
        var totalShare: Double? = null,

        @Column(nullable = false, precision = 18, scale = 2)
        var flowShare: Double? = null) {
    data class Level1BasicId(
            var stockId: String? = null,
            var date: Date? = null) : Serializable
}
