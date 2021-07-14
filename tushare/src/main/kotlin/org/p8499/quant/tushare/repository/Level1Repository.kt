package org.p8499.quant.tushare.repository

import org.p8499.quant.tushare.dao.Level1Dao
import org.p8499.quant.tushare.entity.Level1
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface Level1Repository :
        JpaRepository<Level1, Long>,
        JpaSpecificationExecutor<Level1>,
        PagingAndSortingRepository<Level1, Long>,
        Level1Dao
