package org.p8499.quant.analysis.repository

import org.p8499.quant.analysis.dao.StockIndexQuarterDao
import org.p8499.quant.analysis.entity.StockIndexQuarter
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface StockIndexQuarterRepository :
        JpaRepository<StockIndexQuarter, StockIndexQuarter.StockIndexQuarterId>,
        JpaSpecificationExecutor<StockIndexQuarter>,
        PagingAndSortingRepository<StockIndexQuarter, StockIndexQuarter.StockIndexQuarterId>,
        StockIndexQuarterDao