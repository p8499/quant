package org.p8499.quant.analysis.repository

import org.p8499.quant.analysis.dao.StockIndexDayDao
import org.p8499.quant.analysis.entity.StockIndexDay
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface StockIndexDayRepository :
        JpaRepository<StockIndexDay, StockIndexDay.StockIndexDayId>,
        JpaSpecificationExecutor<StockIndexDay>,
        PagingAndSortingRepository<StockIndexDay, StockIndexDay.StockIndexDayId>,
        StockIndexDayDao