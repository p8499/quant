package org.p8499.quant.analysis.repository

import org.p8499.quant.analysis.dao.SecurityIndexDayDao
import org.p8499.quant.analysis.entity.SecurityIndexDay
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface SecurityIndexDayRepository :
        JpaRepository<SecurityIndexDay, SecurityIndexDay.SecurityIndexDayId>,
        JpaSpecificationExecutor<SecurityIndexDay>,
        PagingAndSortingRepository<SecurityIndexDay, SecurityIndexDay.SecurityIndexDayId>,
        SecurityIndexDayDao {
    fun deleteByRegionAndIdAndType(region: String, id: String, type: String): Int
}