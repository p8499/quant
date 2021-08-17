package org.p8499.quant.analysis.repository

import org.p8499.quant.analysis.dao.GroupIndexDailyDao
import org.p8499.quant.analysis.entity.GroupIndexDaily
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface GroupIndexDailyRepository :
        JpaRepository<GroupIndexDaily, Long>,
        JpaSpecificationExecutor<GroupIndexDaily>,
        PagingAndSortingRepository<GroupIndexDaily, Long>,
        GroupIndexDailyDao