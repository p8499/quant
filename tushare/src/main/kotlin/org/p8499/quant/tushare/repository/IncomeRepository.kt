package org.p8499.quant.tushare.repository

import org.p8499.quant.tushare.dao.IncomeDao
import org.p8499.quant.tushare.entity.Income
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface IncomeRepository :
        JpaRepository<Income, Long>,
        JpaSpecificationExecutor<Income>,
        PagingAndSortingRepository<Income, Long>,
        IncomeDao
