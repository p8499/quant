package org.p8499.quant.tushare.entity

import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "F91")
open class Controller(
        @Id
        @Column(nullable = false, length = 32)
        open var objectId: String? = null,

        @Column(nullable = true, name = "snapshot")
        open var snapshot: LocalDateTime? = null,

        @Column(nullable = true, name = "begin")
        open var begin: LocalDateTime? = null,

        @Column(nullable = true, name = "end")
        open var end: LocalDateTime? = null)
