package org.p8499.quant.analysis.repository

import org.p8499.quant.analysis.dao.StockIndexDailyDao
import org.p8499.quant.analysis.entity.StockIndexDaily
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface StockIndexDailyRepository :
        JpaRepository<StockIndexDaily, Long>,
        JpaSpecificationExecutor<StockIndexDaily>,
        PagingAndSortingRepository<StockIndexDaily, Long>,
        StockIndexDailyDao