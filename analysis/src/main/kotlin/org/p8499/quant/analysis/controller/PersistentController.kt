package org.p8499.quant.analysis.controller

import org.p8499.quant.analysis.dto.SecurityDayDto
import org.p8499.quant.analysis.dto.SecurityDto
import org.p8499.quant.analysis.dto.SecurityQuarterDto
import org.p8499.quant.analysis.entity.*
import org.p8499.quant.analysis.service.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping(path = ["/persistent"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
class PersistentController {
    @Autowired
    protected lateinit var controllerService: ControllerService

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

    @RequestMapping(method = [RequestMethod.POST, RequestMethod.PUT], path = ["/begin"])
    fun begin(@RequestParam region: String, @RequestParam snapshot: LocalDateTime) {
        controllerService.begin(region, snapshot)
    }

    @RequestMapping(method = [RequestMethod.POST, RequestMethod.PUT], path = ["/security"])
    fun saveSecurity(@RequestBody securityDto: SecurityDto) {
        securityService.save(Security(securityDto.region, securityDto.id, securityDto.name))
    }

    @RequestMapping(method = [RequestMethod.POST, RequestMethod.PUT], path = ["/security-day"])
    fun saveSecurityDay(@RequestBody securityDayDto: SecurityDayDto) {
        securityDayDto.indicesList.forEach { indices ->
            securityIndexDayService.saveAll(securityDayDto.region, securityDayDto.id, indices.type, securityDayDto.dateList.mapIndexed { i, date ->
                SecurityIndexDay(securityDayDto.region, securityDayDto.id, indices.type, date, indices.values[i])
            })
        }
        securityDayDto.messagesList.forEach { messages ->
            securityMessageDayService.saveAll(securityDayDto.region, securityDayDto.id, messages.type, securityDayDto.dateList.mapIndexed { i, date ->
                SecurityMessageDay(securityDayDto.region, securityDayDto.id, messages.type, date, messages.values[i])
            })
        }
    }

    @RequestMapping(method = [RequestMethod.POST, RequestMethod.PUT], path = ["/security-quarter"])
    fun saveSecurityQuarter(@RequestBody securityQuarterDto: SecurityQuarterDto) {
        securityQuarterDto.indicesList.forEach { indices ->
            securityIndexQuarterService.saveAll(securityQuarterDto.region, securityQuarterDto.id, indices.type, indices.indexList.map { index ->
                SecurityIndexQuarter(securityQuarterDto.region, securityQuarterDto.id, indices.type, index.publish, index.quarter, index.value)
            })
        }
        securityQuarterDto.messagesList.forEach { messages ->
            securityMessageQuarterService.saveAll(securityQuarterDto.region, securityQuarterDto.id, messages.type, messages.messageList.map { message ->
                SecurityMessageQuarter(securityQuarterDto.region, securityQuarterDto.id, messages.type, message.publish, message.quarter, message.value)
            })
        }
    }

    @RequestMapping(method = [RequestMethod.POST, RequestMethod.PUT], path = ["/end"])
    fun end(@RequestParam region: String) {
        controllerService.end(region)
    }
}