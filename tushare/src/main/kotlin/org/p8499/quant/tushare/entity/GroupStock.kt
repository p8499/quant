package org.p8499.quant.tushare.entity

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "F05")
@IdClass(GroupStock.GroupStockId::class)
data class GroupStock(
        @Id
        @Column(nullable = false, length = 32)
        var groupId: String? = null,

        @Id
        @Column(nullable = false, length = 16)
        var stockId: String? = null,

        @Column(nullable = false, columnDefinition = "NUMBER(8, 4)", precision = 8, scale = 4)
        var weight: Double? = null) {
    data class GroupStockId(
            var groupId: String? = null,
            var stockId: String? = null) : Serializable
}
