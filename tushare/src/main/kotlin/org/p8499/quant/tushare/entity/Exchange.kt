package org.p8499.quant.tushare.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "F01")
open class Exchange(
        @Id
        @Column(nullable = false, length = 4)
        open var id: String? = null,

        @Column(nullable = false, length = 8)
        open var name: String? = null)

