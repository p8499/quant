package org.p8499.quant.analysis.repository

import org.p8499.quant.analysis.dao.SecurityDao
import org.p8499.quant.analysis.entity.Security
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface SecurityRepository :
        JpaRepository<Security, Security.SecurityId>,
        JpaSpecificationExecutor<Security>,
        PagingAndSortingRepository<Security, Security.SecurityId>,
        SecurityDao