package org.p8499.quant.tushare.repository

import org.p8499.quant.tushare.dao.ExpressDao
import org.p8499.quant.tushare.entity.Express
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ExpressRepository :
        JpaRepository<Express, Express.ExpressId>,
        JpaSpecificationExecutor<Express>,
        PagingAndSortingRepository<Express, Express.ExpressId>,
        ExpressDao
