package org.p8499.quant.tushare.repository

import org.p8499.quant.tushare.entity.BalanceSheet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface BalanceSheetRepository :
        JpaRepository<BalanceSheet, Long>,
        JpaSpecificationExecutor<BalanceSheet>,
        PagingAndSortingRepository<BalanceSheet, Long>
