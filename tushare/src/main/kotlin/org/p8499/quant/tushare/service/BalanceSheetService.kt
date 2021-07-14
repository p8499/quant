package org.p8499.quant.tushare.service

import org.p8499.quant.tushare.entity.BalanceSheet
import org.p8499.quant.tushare.repository.BalanceSheetRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class BalanceSheetService {
    @Autowired
    lateinit var balanceSheetRepository: BalanceSheetRepository

    fun saveAll(entityList: List<BalanceSheet>) = balanceSheetRepository.saveAll(entityList)
}