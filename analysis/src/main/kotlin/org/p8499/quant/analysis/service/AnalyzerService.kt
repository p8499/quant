package org.p8499.quant.analysis.service

import org.p8499.quant.analysis.analyzer.RegionAnalyzer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class AnalyzerService {
    protected val logger by lazy { LoggerFactory.getLogger(javaClass) }

    @Autowired
    protected lateinit var securityService: SecurityService

    @Autowired
    protected lateinit var securityIndexDayService: SecurityIndexDayService

    @Autowired
    protected lateinit var securityMessageDayService: SecurityMessageDayService

    @Autowired
    protected lateinit var securityIndexQuarterService: SecurityIndexQuarterService

    @Autowired
    protected lateinit var securityMessageQuarterService: SecurityMessageQuarterService

    fun region(region: String, from: LocalDate, to: LocalDate) = RegionAnalyzer(region, from, to, securityService, securityIndexDayService, securityMessageDayService, securityIndexQuarterService, securityMessageQuarterService)
}