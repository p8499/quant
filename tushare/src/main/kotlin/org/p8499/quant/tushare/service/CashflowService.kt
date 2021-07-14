package org.p8499.quant.tushare.service

import org.p8499.quant.tushare.entity.Cashflow
import org.p8499.quant.tushare.repository.CashflowRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CashflowService {
    @Autowired
    lateinit var cashflowRepository: CashflowRepository

    fun saveAll(entityList: List<Cashflow>) = cashflowRepository.saveAll(entityList)
}