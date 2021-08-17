package org.p8499.quant.analysis.entity

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "S")
@IdClass(Stock.StockId::class)
data class Stock(
        @Id
        @Column(nullable = false, length = 2)
        var region: String? = null,

        @Id
        @Column(nullable = false, length = 32)
        var id: String? = null,

        @Column(nullable = false, length = 64)
        var name: String? = null,

        @Lob
        @Column(nullable = true, columnDefinition = "CLOB")
        var message: String? = null) {
    data class StockId(
            var region: String? = null,
            var id: String? = null) : Serializable
}