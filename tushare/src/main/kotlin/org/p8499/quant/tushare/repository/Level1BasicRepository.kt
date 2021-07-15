package org.p8499.quant.tushare.repository

import org.p8499.quant.tushare.dao.Level1BasicDao
import org.p8499.quant.tushare.entity.Level1Basic
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface Level1BasicRepository :
        JpaRepository<Level1Basic, Long>,
        JpaSpecificationExecutor<Level1Basic>,
        PagingAndSortingRepository<Level1Basic, Long>,
        Level1BasicDao
