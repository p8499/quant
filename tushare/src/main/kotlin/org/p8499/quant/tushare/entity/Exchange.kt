package org.p8499.quant.tushare.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "F01")
data class Exchange(
        @Id
        @Column(nullable = false, length = 4)
        var id: String? = null,

        @Column(nullable = false, length = 8)
        var name: String? = null)

