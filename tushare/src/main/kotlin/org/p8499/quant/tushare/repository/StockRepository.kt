package org.p8499.quant.tushare.repository

import org.p8499.quant.tushare.entity.Stock
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface StockRepository :
        JpaRepository<Stock, String>,
        JpaSpecificationExecutor<Stock>,
        PagingAndSortingRepository<Stock, String>
