package org.p8499.quant.tushare.entity

import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "F061")
@IdClass(BalanceSheet.BalanceSheetId::class)
data class BalanceSheet(
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
         * 股东权益合计(不含少数股东权益) -> 净资产
         */
        @Column(nullable = true, precision = 26, scale = 2)
        var totalHldrEqyExcMinInt: Double? = null) {
    data class BalanceSheetId(
            var stockId: String? = null,
            var year: Int? = null,
            var period: Int? = null) : Serializable
}
