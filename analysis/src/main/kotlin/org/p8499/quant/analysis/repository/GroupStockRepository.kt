package org.p8499.quant.analysis.repository

import org.p8499.quant.analysis.dao.GroupStockDao
import org.p8499.quant.analysis.entity.GroupStock
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface GroupStockRepository :
        JpaRepository<GroupStock, Long>,
        JpaSpecificationExecutor<GroupStock>,
        PagingAndSortingRepository<GroupStock, Long>,
        GroupStockDao