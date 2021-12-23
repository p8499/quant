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
    protected lateinit var stockIndexDailyService: StockIndexDailyService

    @Autowired
    protected lateinit var stockMessageDailyService: StockMessageDailyService

    @Autowired
    protected lateinit var groupService: GroupService

    @Autowired
    protected lateinit var groupIndexDailyService: GroupIndexDailyService

    @Autowired
    protected lateinit var groupMessageDailyService: GroupMessageDailyService

    @Autowired
    protected lateinit var groupStockService: GroupStockService

    @Autowired
    protected lateinit var controllerService: ControllerService

    @Transactional
    fun saveStock(region: String, id: String,
                  stock: Stock,
                  openDailyIterable: Iterable<StockIndexDaily>,
                  closeDailyIterable: Iterable<StockIndexDaily>,
                  highDailyIterable: Iterable<StockIndexDaily>,
                  lowDailyIterable: Iterable<StockIndexDaily>,
                  volumeDailyIterable: Iterable<StockIndexDaily>,
                  amountDailyIterable: Iterable<StockIndexDaily>,
                  flowShareDailyIterable: Iterable<StockIndexDaily>,
                  totalShareDailyIterable: Iterable<StockIndexDaily>,
                  flowValueDailyIterable: Iterable<StockIndexDaily>,
                  totalValueDailyIterable: Iterable<StockIndexDaily>,
                  assetDailyIterable: Iterable<StockIndexDaily>,
                  profitDailyIterable: Iterable<StockIndexDaily>,
                  revenueDailyIterable: Iterable<StockIndexDaily>,
                  cashflowDailyIterable: Iterable<StockIndexDaily>,
                  pbDailyIterable: Iterable<StockIndexDaily>,
                  peDailyIterable: Iterable<StockIndexDaily>,
                  psDailyIterable: Iterable<StockIndexDaily>,
                  pcfDailyIterable: Iterable<StockIndexDaily>,
                  messageDailyIterable: Iterable<StockMessageDaily>) {
        logger.info("${stock.id} Saving Start")
        val time0 = System.currentTimeMillis()
        stockService.delete(region, id)
        stockIndexDailyService.delete(region, id)
        stockService.save(stock)
        stockIndexDailyService.saveAll(openDailyIterable)
        stockIndexDailyService.saveAll(closeDailyIterable)
        stockIndexDailyService.saveAll(highDailyIterable)
        stockIndexDailyService.saveAll(lowDailyIterable)
        stockIndexDailyService.saveAll(volumeDailyIterable)
        stockIndexDailyService.saveAll(amountDailyIterable)
        stockIndexDailyService.saveAll(flowShareDailyIterable)
        stockIndexDailyService.saveAll(totalShareDailyIterable)
        stockIndexDailyService.saveAll(flowValueDailyIterable)
        stockIndexDailyService.saveAll(totalValueDailyIterable)
        stockIndexDailyService.saveAll(assetDailyIterable)
        stockIndexDailyService.saveAll(profitDailyIterable)
        stockIndexDailyService.saveAll(revenueDailyIterable)
        stockIndexDailyService.saveAll(cashflowDailyIterable)
        stockIndexDailyService.saveAll(pbDailyIterable)
        stockIndexDailyService.saveAll(peDailyIterable)
        stockIndexDailyService.saveAll(psDailyIterable)
        stockIndexDailyService.saveAll(pcfDailyIterable)
        stockMessageDailyService.saveAll(messageDailyIterable)
        val time1 = System.currentTimeMillis()
        logger.info("${stock.id} Saving Finish, time = ${time1 - time0} milliseconds")
    }

    @Transactional
    fun saveGroup(region: String, id: String,
                  group: Group,
                  groupStockIterable: Iterable<GroupStock>,
                  openDailyIterable: Iterable<GroupIndexDaily>,
                  closeDailyIterable: Iterable<GroupIndexDaily>,
                  highDailyIterable: Iterable<GroupIndexDaily>,
                  lowDailyIterable: Iterable<GroupIndexDaily>,
                  volumeDailyIterable: Iterable<GroupIndexDaily>,
                  amountDailyIterable: Iterable<GroupIndexDaily>,
                  flowShareDailyIterable: Iterable<GroupIndexDaily>,
                  totalShareDailyIterable: Iterable<GroupIndexDaily>,
                  flowValueDailyIterable: Iterable<GroupIndexDaily>,
                  totalValueDailyIterable: Iterable<GroupIndexDaily>,
                  pbDailyIterable: Iterable<GroupIndexDaily>,
                  peDailyIterable: Iterable<GroupIndexDaily>,
                  psDailyIterable: Iterable<GroupIndexDaily>,
                  pcfDailyIterable: Iterable<GroupIndexDaily>,
                  messageDailyIterable: Iterable<GroupMessageDaily>) {
        logger.info("${group.id} Saving Start")
        val time0 = System.currentTimeMillis()
        groupService.delete(region, id)
        groupStockService.deleteByGroup(region, id)
        groupIndexDailyService.delete(region, id)
        groupService.save(group)
        groupStockService.saveAll(groupStockIterable)
        groupIndexDailyService.saveAll(openDailyIterable)
        groupIndexDailyService.saveAll(closeDailyIterable)
        groupIndexDailyService.saveAll(highDailyIterable)
        groupIndexDailyService.saveAll(lowDailyIterable)
        groupIndexDailyService.saveAll(volumeDailyIterable)
        groupIndexDailyService.saveAll(amountDailyIterable)
        groupIndexDailyService.saveAll(flowShareDailyIterable)
        groupIndexDailyService.saveAll(totalShareDailyIterable)
        groupIndexDailyService.saveAll(flowValueDailyIterable)
        groupIndexDailyService.saveAll(totalValueDailyIterable)
        groupIndexDailyService.saveAll(pbDailyIterable)
        groupIndexDailyService.saveAll(peDailyIterable)
        groupIndexDailyService.saveAll(psDailyIterable)
        groupIndexDailyService.saveAll(pcfDailyIterable)
        groupMessageDailyService.saveAll(messageDailyIterable)
        val time1 = System.currentTimeMillis()
        logger.info("${group.id} Saving Finish, time = ${time1 - time0} milliseconds")
    }

    fun complete(region: String) {
        controllerService.complete(region)
    }
}