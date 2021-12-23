package org.p8499.quant.analysis.entity

import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "GS")
@IdClass(GroupStock.GroupStockId::class)
open class GroupStock(
        @Id
        @Column(nullable = false, length = 2)
        open var region: String? = null,

        @Id
        @Column(nullable = false, length = 32)
        open var groupId: String? = null,

        @Id
        @Column(nullable = false, length = 32)
        open var stockId: String? = null,

        @Id
        @Column(nullable = false, columnDefinition = "NUMBER(5, 4)", precision = 5, scale = 4)
        open var percent: Double? = null) {
    open class GroupStockId(
            open var region: String? = null,
            open var groupId: String? = null,
            open var stockId: String? = null) : Serializable {
        override fun hashCode(): Int = Objects.hash(region, groupId, stockId)
        override fun equals(other: Any?): Boolean = other is GroupStockId && other.region == region && other.groupId == groupId && other.stockId == stockId
    }
}