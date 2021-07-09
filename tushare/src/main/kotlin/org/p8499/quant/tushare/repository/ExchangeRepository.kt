package org.p8499.quant.tushare.repository

import org.p8499.quant.tushare.entity.Exchange
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ExchangeRepository :
        JpaRepository<Exchange, String>,
        JpaSpecificationExecutor<Exchange>,
        PagingAndSortingRepository<Exchange, String>
