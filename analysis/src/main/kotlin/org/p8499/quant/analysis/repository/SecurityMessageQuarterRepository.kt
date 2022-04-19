package org.p8499.quant.analysis.repository

import org.p8499.quant.analysis.dao.SecurityMessageQuarterDao
import org.p8499.quant.analysis.entity.SecurityMessageQuarter
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface SecurityMessageQuarterRepository :
        JpaRepository<SecurityMessageQuarter, SecurityMessageQuarter.SecurityMessageQuarterId>,
        JpaSpecificationExecutor<SecurityMessageQuarter>,
        PagingAndSortingRepository<SecurityMessageQuarter, SecurityMessageQuarter.SecurityMessageQuarterId>,
        SecurityMessageQuarterDao {
    fun deleteByRegionAndIdAndType(region: String, id: String, type: String): Int
}