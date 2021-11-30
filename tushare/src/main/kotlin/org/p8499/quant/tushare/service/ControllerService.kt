package org.p8499.quant.tushare.service

import org.p8499.quant.tushare.entity.Controller
import org.p8499.quant.tushare.repository.ControllerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class ControllerService {
    @Autowired
    protected lateinit var controlerRepository: ControllerRepository

    operator fun get(objectId: String): Controller? = controlerRepository.getById(objectId)

    fun complete(objectId: String): Controller = controlerRepository.saveAndFlush(Controller(objectId, Date()))
}