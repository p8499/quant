package org.p8499.quant.analysis.repository

import org.p8499.quant.analysis.dao.GroupDao
import org.p8499.quant.analysis.entity.Group
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface GroupRepository :
        JpaRepository<Group, Long>,
        JpaSpecificationExecutor<Group>,
        PagingAndSortingRepository<Group, Long>,
        GroupDao