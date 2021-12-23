package org.p8499.quant.analysis.entity

import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "G")
@IdClass(Group.GroupId::class)
open class Group(
        @Id
        @Column(nullable = false, length = 2)
        open var region: String? = null,

        @Id
        @Column(nullable = false, length = 32)
        open var id: String? = null,

        @Column(nullable = false, length = 64)
        open var name: String? = null) {
    open class GroupId(
            open var region: String? = null,
            open var id: String? = null) : Serializable {
        override fun hashCode(): Int = Objects.hash(region, id)
        override fun equals(other: Any?): Boolean = other is GroupId && other.region == region && other.id == id
    }
}