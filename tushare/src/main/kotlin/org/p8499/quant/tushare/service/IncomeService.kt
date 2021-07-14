package org.p8499.quant.tushare.service

import org.p8499.quant.tushare.entity.Income
import org.p8499.quant.tushare.repository.IncomeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class IncomeService {
    @Autowired
    lateinit var incomeRepository: IncomeRepository

    fun saveAll(entityList: List<Income>) = incomeRepository.saveAll(entityList)
}