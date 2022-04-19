package org.p8499.quant.analysis.entity

import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "C")
open class Controller(
        @Id
        @Column(nullable = false, length = 2)
        open var region: String? = null,

        @Column(nullable = true, name = "snapshot")
        open var snapshot: LocalDateTime? = null,

        @Column(nullable = true, name = "begin")
        open var begin: LocalDateTime? = null,

        @Column(nullable = true, name = "end")
        open var end: LocalDateTime? = null
)