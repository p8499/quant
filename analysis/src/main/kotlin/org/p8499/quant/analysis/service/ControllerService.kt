package org.p8499.quant.analysis.service

import org.p8499.quant.analysis.entity.Controller
import org.p8499.quant.analysis.repository.ControllerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ControllerService {
    @Autowired
    protected lateinit var controllerRepository: ControllerRepository

    operator fun get(region: String): Controller? = controllerRepository.findByIdOrNull(region)

    fun begin(region: String, snapshot: LocalDateTime): Controller = controllerRepository.saveAndFlush(Controller(region, snapshot, LocalDateTime.now(), null))

    fun end(region: String): Controller? = get(region)?.also { it.end = LocalDateTime.now() }?.run(controllerRepository::saveAndFlush)
}