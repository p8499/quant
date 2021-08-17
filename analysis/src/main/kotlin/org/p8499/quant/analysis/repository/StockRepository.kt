package org.p8499.quant.analysis.repository

import org.p8499.quant.analysis.dao.StockDao
import org.p8499.quant.analysis.entity.Stock
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface StockRepository :
        JpaRepository<Stock, Long>,
        JpaSpecificationExecutor<Stock>,
        PagingAndSortingRepository<Stock, Long>,
        StockDao