package org.p8499.quant.analysis

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableEurekaClient
@EnableTransactionManagement
class AnalysisApplication

fun main(args: Array<String>) {
    runApplication<AnalysisApplication>(*args)
}
