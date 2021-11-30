package org.p8499.quant.analysis.repository

import org.p8499.quant.analysis.entity.Controller
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ControllerRepository :
        JpaRepository<Controller, String>,
        JpaSpecificationExecutor<Controller>,
        PagingAndSortingRepository<Controller, String>