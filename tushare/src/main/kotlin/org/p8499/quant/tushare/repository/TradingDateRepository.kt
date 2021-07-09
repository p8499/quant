package org.p8499.quant.tushare.repository

import org.p8499.quant.tushare.entity.TradingDate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface TradingDateRepository :
        JpaRepository<TradingDate, Long>,
        JpaSpecificationExecutor<TradingDate>,
        PagingAndSortingRepository<TradingDate, Long>
