package org.p8499.quant.analysis.entity

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "GS")
@IdClass(GroupStock.GroupStockId::class)
data class GroupStock(
        @Id
        @Column(nullable = false, length = 2)
        var region: String? = null,

        @Id
        @Column(nullable = false, length = 32)
        var groupId: String? = null,

        @Id
        @Column(nullable = false, length = 32)
        var stockId: String? = null,

        @Id
        @Column(nullable = false, columnDefinition = "NUMBER(5, 4)", precision = 5, scale = 4)
        var percent: Double? = null) {
    data class GroupStockId(
            var region: String? = null,
            var groupId: String? = null,
            var stockId: String? = null) : Serializable
}