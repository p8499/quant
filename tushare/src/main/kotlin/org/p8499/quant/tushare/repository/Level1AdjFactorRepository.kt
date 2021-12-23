package org.p8499.quant.tushare.repository

import org.p8499.quant.tushare.dao.Level1AdjFactorDao
import org.p8499.quant.tushare.entity.Level1AdjFactor
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface Level1AdjFactorRepository :
        JpaRepository<Level1AdjFactor, Level1AdjFactor.Level1AdjFactorId>,
        JpaSpecificationExecutor<Level1AdjFactor>,
        PagingAndSortingRepository<Level1AdjFactor, Level1AdjFactor.Level1AdjFactorId>,
        Level1AdjFactorDao
