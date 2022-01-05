package org.p8499.quant.analysis.repository

import org.p8499.quant.analysis.dao.GroupIndexDayDao
import org.p8499.quant.analysis.entity.GroupIndexDay
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface GroupIndexDayRepository :
        JpaRepository<GroupIndexDay, GroupIndexDay.GroupIndexDayId>,
        JpaSpecificationExecutor<GroupIndexDay>,
        PagingAndSortingRepository<GroupIndexDay, GroupIndexDay.GroupIndexDayId>,
        GroupIndexDayDao