package org.p8499.quant.analysis.repository

import org.p8499.quant.analysis.dao.SecurityIndexQuarterDao
import org.p8499.quant.analysis.entity.SecurityIndexQuarter
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface SecurityIndexQuarterRepository :
        JpaRepository<SecurityIndexQuarter, SecurityIndexQuarter.SecurityIndexQuarterId>,
        JpaSpecificationExecutor<SecurityIndexQuarter>,
        PagingAndSortingRepository<SecurityIndexQuarter, SecurityIndexQuarter.SecurityIndexQuarterId>,
        SecurityIndexQuarterDao {
    fun deleteByRegionAndIdAndType(region: String, id: String, type: String): Int
}