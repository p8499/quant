package org.p8499.quant.analysis.repository

import org.p8499.quant.analysis.dao.GroupMessageDailyDao
import org.p8499.quant.analysis.entity.GroupMessageDaily
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface GroupMessageDailyRepository :
        JpaRepository<GroupMessageDaily, GroupMessageDaily.GroupMessageDailyId>,
        JpaSpecificationExecutor<GroupMessageDaily>,
        PagingAndSortingRepository<GroupMessageDaily, GroupMessageDaily.GroupMessageDailyId>,
        GroupMessageDailyDao