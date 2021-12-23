package org.p8499.quant.tushare.service.tushareSynchronizer

import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import org.p8499.quant.tushare.TushareApplication
import org.p8499.quant.tushare.common.let
import org.p8499.quant.tushare.common.mapNotNull
import org.p8499.quant.tushare.entity.Group
import org.p8499.quant.tushare.entity.GroupStock
import org.p8499.quant.tushare.entity.Stock
import org.p8499.quant.tushare.service.*
import org.p8499.quant.tushare.service.tushareRequest.ConceptDetailRequest
import org.p8499.quant.tushare.service.tushareRequest.IndexMemberRequest
import org.p8499.quant.tushare.service.tushareRequest.IndexWeightRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.concurrent.TimeUnit

@Service
class GroupStockSynchronizer {
    val logger by lazy { LoggerFactory.getLogger(TushareApplication::class.java) }

    @Autowired
    protected lateinit var tradingDateService: TradingDateService

    @Autowired
    protected lateinit var stockService: StockService

    @Autowired
    protected lateinit var groupService: GroupService

    @Autowired
    protected lateinit var groupStockService: GroupStockService

    @Autowired
    protected lateinit var level1BasicService: Level1BasicService

    @Autowired
    protected lateinit var controllerService: ControllerService

    @Autowired
    protected lateinit var indexWeightRequest: IndexWeightRequest

    @Autowired
    protected lateinit var indexMemberRequest: IndexMemberRequest

    @Autowired
    protected lateinit var conceptDetailRequest: ConceptDetailRequest

    fun invoke() {
        logger.info("Start Synchronizing GroupStock")
        val groupStockListOfIndex: (LocalDate) -> List<GroupStock> = { tradeDate ->
            val groupStockList = indexWeightRequest.invoke(IndexWeightRequest.InParams(tradeDate = tradeDate), IndexWeightRequest.OutParams::class.java).map { GroupStock(it.indexCode, it.conCode, it.weight) }
            val stockIdList = stockService.findAll().map(Stock::id)
            groupStockList.filter { stockIdList.contains(it.stockId) }
        }
        val flowShareMap: (LocalDate) -> Map<String, Double> = { tradeDate ->
            val stockIdFlowable = Flowable.fromIterable(stockService.findAll()).mapNotNull(Stock::id)
            val flowShareFlowable = stockIdFlowable.parallel(3).runOn(Schedulers.io()).mapNotNull { level1BasicService[it, tradeDate]?.flowShare }.sequential()
            Flowable.zip(stockIdFlowable, flowShareFlowable) { stockId, flowShare -> Pair(stockId, flowShare) }
                    .collect({ mutableMapOf<String, Double>() }, { map, pair -> map[pair.first] = pair.second }).blockingGet()
        }
        val weightList: (Map<String, Double>, List<String>) -> List<Double> = { rsMap, stockIdList ->
            val flowShareList = stockIdList.map { stockId -> rsMap[stockId] }
            val maxFlowShare = flowShareList.mapNotNull { it }.maxOrNull()
            flowShareList.map { let(it, maxFlowShare) { a, b -> a * 1000 / b } ?: 0.0 }
        }
        val groupStockListOfTransform: (Map<String, Double>, Int, (String) -> List<String>) -> List<GroupStock> = { rsMap, times, transform ->
            val start = System.currentTimeMillis()
            val groupIdList = groupService.findByType(Group.Type.INDUSTRY).map(Group::id)
            val groupIdFlowable = Flowable.fromIterable(groupIdList)
            val stockIdListFlowable = groupIdFlowable.map(transform).zipWith(Flowable.interval((60 * 1000 / times).toLong(), TimeUnit.MILLISECONDS)) { stockIdList, _ -> stockIdList }.onBackpressureBuffer(8192)
            val wListFlowable = stockIdListFlowable.map { ;weightList(rsMap, it) }
            Flowable.zip(groupIdFlowable, stockIdListFlowable, wListFlowable) { groupId, stockIdList, wList -> Triple(groupId, stockIdList, wList) }
                    .map { it.second.mapIndexed { index, s -> GroupStock(it.first, s, it.third[index]) } }
                    .collect({ mutableListOf<GroupStock>() }, { all, group -> all += group }).blockingGet()
        }
        val groupStockListOfIndustry: (Map<String, Double>) -> List<GroupStock> = { rsMap ->
            groupStockListOfTransform(rsMap, 200) { indexMemberRequest.invoke(IndexMemberRequest.InParams(indexCode = it), IndexMemberRequest.OutParams::class.java).mapNotNull(IndexMemberRequest.OutParams::conCode) }
        }
        val groupStockListOfConcept: (Map<String, Double>) -> List<GroupStock> = { rsMap ->
            groupStockListOfTransform(rsMap, 200) { conceptDetailRequest.invoke(ConceptDetailRequest.InParams(id = it), ConceptDetailRequest.OutParams::class.java).mapNotNull(ConceptDetailRequest.OutParams::tsCode) }
        }
        val date = tradingDateService.last("SSE")?.date ?: return
        val fsMap = flowShareMap(date)
        groupStockService.deleteAndSaveAll(groupStockListOfIndex(date) + groupStockListOfIndustry(fsMap) + groupStockListOfConcept(fsMap))
        controllerService.complete("GroupStock")
        logger.info("Finish Synchronizing GroupStock")
    }
}