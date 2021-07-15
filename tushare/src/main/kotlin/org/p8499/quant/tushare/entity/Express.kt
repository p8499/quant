package org.p8499.quant.tushare.entity

import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "F06E")
@IdClass(Express.ExpressId::class)
data class Express(
        @Id
        @Column(nullable = false, length = 16)
        var stockId: String? = null,

        @Id
        @Column(nullable = false)
        var year: Int? = null,

        @Id
        @Column(nullable = false)
        var period: Int? = null,

        @Column(nullable = false)
        @Temporal(TemporalType.DATE)
        var publish: Date? = null,

        /**
         * 股东权益合计(不含少数股东权益)(元) -> 净资产
         */
        @Column(nullable = false, precision = 26, scale = 2)
        var totalHldrEqyExcMinInt: Double? = null,

        /**
         * 营业收入(元) -> 营业收入(年初至今)
         */
        @Column(nullable = false, precision = 26, scale = 2)
        var revenue: Double? = null) {
    data class ExpressId(
            var stockId: String? = null,
            var year: Int? = null,
            var period: Int? = null) : Serializable
}

