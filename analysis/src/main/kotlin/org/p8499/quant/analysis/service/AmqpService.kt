package org.p8499.quant.analysis.service

//import org.springframework.amqp.core.Message
//import org.springframework.amqp.rabbit.annotation.RabbitHandler
//import org.springframework.amqp.rabbit.annotation.RabbitListener
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.stereotype.Service
//
//@Service
//class AmqpService {
//    @Autowired
//    lateinit var redisService: RedisService
//
//    @RabbitListener(queues = ["quant"])
//    @RabbitHandler
//    fun consume(message: Message) {
//        redisService.reload(String(message.body))
//    }
//}