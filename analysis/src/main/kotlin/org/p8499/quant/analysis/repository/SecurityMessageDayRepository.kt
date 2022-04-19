package org.p8499.quant.analysis.repository

import org.p8499.quant.analysis.dao.SecurityMessageDayDao
import org.p8499.quant.analysis.entity.SecurityMessageDay
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface SecurityMessageDayRepository :
        JpaRepository<SecurityMessageDay, SecurityMessageDay.SecurityMessageDayId>,
        JpaSpecificationExecutor<SecurityMessageDay>,
        PagingAndSortingRepository<SecurityMessageDay, SecurityMessageDay.SecurityMessageDayId>,
        SecurityMessageDayDao {
    fun deleteByRegionAndIdAndType(region: String, id: String, type: String): Int
}