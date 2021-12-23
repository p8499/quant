package org.p8499.quant.analysis.service

import org.p8499.quant.analysis.entity.Controller
import org.p8499.quant.analysis.repository.ControllerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ControllerService {
    @Autowired
    protected lateinit var controllerRepository: ControllerRepository

    fun complete(region: String): Controller = controllerRepository.saveAndFlush(Controller(region, LocalDateTime.now()))
}