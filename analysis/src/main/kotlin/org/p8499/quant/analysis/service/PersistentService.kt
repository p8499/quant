package org.p8499.quant.analysis.service

import org.p8499.quant.analysis.entity.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PersistentService {
    protected val logger by lazy { LoggerFactory.getLogger(javaClass) }

    @Autowired
    protected lateinit var stockService: StockService

    @Autowired
    protected lateinit var stockIndexDayService: StockIndexDayService

    @Autowired
    protected lateinit var stockMessageDayService: StockMessageDayService

    @Autowired
    protected lateinit var stockIndexQuarterService: StockIndexQuarterService

    @Autowired
    protected lateinit var groupService: GroupService

    @Autowired
    protected lateinit var groupIndexDayService: GroupIndexDayService

    @Autowired
    protected lateinit var groupMessageDayService: GroupMessageDayService

    @Autowired
    protected lateinit var groupStockService: GroupStockService

    @Autowired
    protected lateinit var controllerService: ControllerService

    @Transactional
    fun saveStock(region: String, id: String, stock: Stock) {
        stockService.delete(region, id)
        stockService.save(stock)
    }

    @Transactional
    fun saveStockIndexDay(region: String, id: String, vararg stockIndexDayIterables: Iterable<StockIndexDay>) {
        stockIndexDayService.delete(region, id)
        for (stockIndexDayIterable in stockIndexDayIterables)
            stockIndexDayService.saveAll(stockIndexDayIterable)
    }

    @Transactional
    fun saveStockMessageDay(region: String, id: String, vararg stockMessageDayIterables: Iterable<StockMessageDay>) {
        stockMessageDayService.delete(region, id)
        for (stockMessageDayIterable in stockMessageDayIterables)
            stockMessageDayService.saveAll(stockMessageDayIterable)
    }

    @Transactional
    fun saveStockIndexQuarter(region: String, id: String, vararg stockIndexQuarterIterables: Iterable<StockIndexQuarter>) {
        stockIndexQuarterService.delete(region, id)
        for (stockIndexQuarterIterable in stockIndexQuarterIterables)
            stockIndexQuarterService.saveAll(stockIndexQuarterIterable)
    }

    @Transactional
    fun saveGroup(region: String, id: String, group: Group) {
        groupService.delete(region, id)
        groupService.save(group)
    }

    @Transactional
    fun saveGroupIndexDay(region: String, id: String, vararg groupIndexDayIterables: Iterable<GroupIndexDay>) {
        groupIndexDayService.delete(region, id)
        for (groupIndexDayIterable in groupIndexDayIterables)
            groupIndexDayService.saveAll(groupIndexDayIterable)
    }

    @Transactional
    fun saveGroupMessageDay(region: String, id: String, vararg groupMessageDayIterables: Iterable<GroupMessageDay>) {
        groupMessageDayService.delete(region, id)
        for (groupMessageDayIterable in groupMessageDayIterables)
            groupMessageDayService.saveAll(groupMessageDayIterable)
    }

    fun complete(region: String) {
        controllerService.complete(region)
    }
}