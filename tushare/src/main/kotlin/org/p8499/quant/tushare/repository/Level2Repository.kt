package org.p8499.quant.tushare.repository

import org.p8499.quant.tushare.dao.Level2Dao
import org.p8499.quant.tushare.entity.Level2
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface Level2Repository :
        JpaRepository<Level2, Level2.Level2Id>,
        JpaSpecificationExecutor<Level2>,
        PagingAndSortingRepository<Level2, Level2.Level2Id>,
        Level2Dao
