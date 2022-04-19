package org.p8499.quant.tushare.service

import org.p8499.quant.tushare.entity.Controller
import org.p8499.quant.tushare.repository.ControllerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ControllerService {
    @Autowired
    protected lateinit var controllerRepository: ControllerRepository

    operator fun get(objectId: String): Controller? = controllerRepository.findByIdOrNull(objectId)

    fun findAll(): List<Controller> = controllerRepository.findAll()

    fun begin(region: String, snapshot: LocalDateTime): Controller = controllerRepository.saveAndFlush(Controller(region, snapshot, LocalDateTime.now(), null))

    fun end(region: String): Controller? = get(region)?.also { it.end = LocalDateTime.now() }?.run(controllerRepository::saveAndFlush)
}