package org.p8499.quant.tushare.repository

import org.p8499.quant.tushare.dao.GroupStockDao
import org.p8499.quant.tushare.entity.GroupStock
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface GroupStockRepository :
        JpaRepository<GroupStock, GroupStock.GroupStockId>,
        JpaSpecificationExecutor<GroupStock>,
        PagingAndSortingRepository<GroupStock, GroupStock.GroupStockId>,
        GroupStockDao
