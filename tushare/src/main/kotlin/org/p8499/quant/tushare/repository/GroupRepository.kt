package org.p8499.quant.tushare.repository

import org.p8499.quant.tushare.dao.GroupDao
import org.p8499.quant.tushare.entity.Group
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface GroupRepository :
        JpaRepository<Group, String>,
        JpaSpecificationExecutor<Group>,
        PagingAndSortingRepository<Group, String>,
        GroupDao
