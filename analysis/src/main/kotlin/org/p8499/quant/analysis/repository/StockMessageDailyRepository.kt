package org.p8499.quant.analysis.repository

import org.p8499.quant.analysis.dao.StockMessageDailyDao
import org.p8499.quant.analysis.entity.StockMessageDaily
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface StockMessageDailyRepository :
        JpaRepository<StockMessageDaily, StockMessageDaily.StockMessageDailyId>,
        JpaSpecificationExecutor<StockMessageDaily>,
        PagingAndSortingRepository<StockMessageDaily, StockMessageDaily.StockMessageDailyId>,
        StockMessageDailyDao