package org.p8499.quant.tushare.service

import org.p8499.quant.tushare.entity.Express
import org.p8499.quant.tushare.repository.ExpressRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ExpressService {
    @Autowired
    lateinit var expressRepository: ExpressRepository

    fun saveAll(entityList: List<Express>) = expressRepository.saveAll(entityList)
}