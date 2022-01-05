package org.p8499.quant.analysis.repository

import org.p8499.quant.analysis.dao.GroupMessageDayDao
import org.p8499.quant.analysis.entity.GroupMessageDay
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface GroupMessageDayRepository :
        JpaRepository<GroupMessageDay, GroupMessageDay.GroupMessageDayId>,
        JpaSpecificationExecutor<GroupMessageDay>,
        PagingAndSortingRepository<GroupMessageDay, GroupMessageDay.GroupMessageDayId>,
        GroupMessageDayDao