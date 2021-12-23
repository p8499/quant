package org.p8499.quant.tushare.repository

import org.p8499.quant.tushare.dao.BalanceSheetDao
import org.p8499.quant.tushare.entity.BalanceSheet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface BalanceSheetRepository :
        JpaRepository<BalanceSheet, BalanceSheet.BalanceSheetId>,
        JpaSpecificationExecutor<BalanceSheet>,
        PagingAndSortingRepository<BalanceSheet, BalanceSheet.BalanceSheetId>,
        BalanceSheetDao
