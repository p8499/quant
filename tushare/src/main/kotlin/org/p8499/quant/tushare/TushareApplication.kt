package org.p8499.quant.tushare

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
class TushareApplication

fun main(args: Array<String>) {
    runApplication<TushareApplication>(*args)
}
