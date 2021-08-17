package org.p8499.quant.tushare.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class WelcomeController {
    @GetMapping(path = ["/welcome"])
    fun index(): String = "Welcome"
}