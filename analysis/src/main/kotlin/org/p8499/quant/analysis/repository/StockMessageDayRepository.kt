package org.p8499.quant.analysis.repository

import org.p8499.quant.analysis.dao.StockMessageDayDao
import org.p8499.quant.analysis.entity.StockMessageDay
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface StockMessageDayRepository :
        JpaRepository<StockMessageDay, StockMessageDay.StockMessageDayId>,
        JpaSpecificationExecutor<StockMessageDay>,
        PagingAndSortingRepository<StockMessageDay, StockMessageDay.StockMessageDayId>,
        StockMessageDayDao