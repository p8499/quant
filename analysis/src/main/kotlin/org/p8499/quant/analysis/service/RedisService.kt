package org.p8499.quant.analysis.service

//import com.fasterxml.jackson.databind.ObjectMapper
//import org.p8499.quant.analysis.entity.Group
//import org.p8499.quant.analysis.entity.Stock
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.data.redis.core.StringRedisTemplate
//import org.springframework.stereotype.Service
//
//@Service
//class RedisService {
//    @Autowired
//    lateinit var stringRedisTemplate: StringRedisTemplate
//
//    @Autowired
//    lateinit var objectMapper: ObjectMapper
//
//    var stockDtoList: List<Stock> = listOf()
//
//    var groupDtoList: List<Group> = listOf()
//
//    fun reload(region: String) {
//        stockDtoList = stringRedisTemplate.keys("${region}S*").map { objectMapper.readValue(stringRedisTemplate.opsForValue()[it], Stock::class.java) }
//        groupDtoList = stringRedisTemplate.keys("${region}G*").map { objectMapper.readValue(stringRedisTemplate.opsForValue()[it], Group::class.java) }
//    }
//
////    fun intersect(groupNameList: List<String>): List<String> {
////        val stockIdsList = mutableListOf<List<String>>()
////        for (groupName in groupNameList) {
////            stockIdsList.add(groupDtoList.filter { it.name.contains(groupName) }.flatMap { it.stockIdList })
////        }
////        return stockIdsList.reduce { acc, list -> acc.intersect(list).toList() }
////    }
//
//    private fun stockIndex(stockId: String, transform: (Stock) -> List<Double?>): List<Double?>? {
//        return stockDtoList.find { it.id == stockId }?.let(transform)
//    }
//}