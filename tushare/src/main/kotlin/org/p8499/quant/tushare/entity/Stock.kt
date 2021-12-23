package org.p8499.quant.tushare.entity

import java.time.LocalDate
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "F03")
open class Stock(
        @Id
        @Column(nullable = false, length = 16)
        open var id: String? = null,

        @Column(nullable = false, length = 4)
        open var exchangeId: String? = null,

        @Column(nullable = false, length = 6)
        open var code: String? = null,

        @Column(nullable = false, length = 16)
        open var name: String? = null,

        @Column(nullable = false)
//        @Temporal(TemporalType.DATE)
        open var listed: LocalDate? = null,

//        @Temporal(TemporalType.DATE)
        open var delisted: LocalDate? = null)
