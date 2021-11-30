package org.p8499.quant.tushare.service.tushareSynchronizer

import org.p8499.quant.tushare.TushareApplication
import org.p8499.quant.tushare.entity.Group
import org.p8499.quant.tushare.service.ControllerService
import org.p8499.quant.tushare.service.GroupService
import org.p8499.quant.tushare.service.tushareRequest.ConceptRequest
import org.p8499.quant.tushare.service.tushareRequest.IndexBasicRequest
import org.p8499.quant.tushare.service.tushareRequest.IndexClassifyRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GroupSynchronizer {
    val logger by lazy { LoggerFactory.getLogger(TushareApplication::class.java) }

    @Autowired
    protected lateinit var groupService: GroupService

    @Autowired
    protected lateinit var controllerService: ControllerService

    @Autowired
    protected lateinit var indexBasicRequest: IndexBasicRequest

    @Autowired
    protected lateinit var indexClassifyRequest: IndexClassifyRequest

    @Autowired
    protected lateinit var conceptRequest: ConceptRequest

    fun invoke() {
        logger.info("Start Synchronizing Group")
        val indexListOfMarket: (String) -> List<Group> = { market ->
            indexBasicRequest.invoke(IndexBasicRequest.InParams(market = market), IndexBasicRequest.OutParams::class.java).map { Group(it.tsCode, it.name, Group.Type.INDEX) }
        }
        val industryList: () -> List<Group> = {
            indexClassifyRequest.invoke(IndexClassifyRequest.InParams(), IndexClassifyRequest.OutParams::class.java).map { Group(it.indexCode, it.industryName, Group.Type.INDUSTRY) }
        }
        val conceptList: () -> List<Group> = {
            conceptRequest.invoke(ConceptRequest.InParams(), ConceptRequest.OutParams::class.java).map { Group(it.code, it.name, Group.Type.CONCEPT) }
        }
        groupService.saveAll(indexListOfMarket("MSCI") + indexListOfMarket("CSI") + indexListOfMarket("SSE") + indexListOfMarket("SZSE") + indexListOfMarket("CICC") + indexListOfMarket("SW") + indexListOfMarket("OTH") + industryList() + conceptList())
        controllerService.complete("Group")
        logger.info("Finish Synchronizing Group")
    }
}