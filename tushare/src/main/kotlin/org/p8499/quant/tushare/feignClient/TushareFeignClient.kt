package org.p8499.quant.tushare.feignClient

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

class TushareRequestBody<T>(
        @get:JsonProperty("api_name") val apiName: String,
        val token: String,
        val params: T,
        val fields: Array<String>)

@JsonDeserialize(using = TushareResponseBodyDeserializer::class)
class TushareResponseBody(
        val code: Int,
        val msg: String,
        val data: Array<Map<String, Any>>)

class TushareResponseBodyDeserializer : JsonDeserializer<TushareResponseBody>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): TushareResponseBody {
        val node = p.codec.readTree<JsonNode>(p)
        val code = node["code"] as IntNode
        val msg = node["msg"] as TextNode
        val data = node["data"] as ObjectNode
        val fields = data["fields"] as ArrayNode
        val items = data["items"] as ArrayNode
        return TushareResponseBody(code.asInt(), msg.asText(), items.map { item ->
            HashMap<String, Any>().apply {
                item.forEachIndexed { index, jsonNode -> put(fields[index].asText(), jsonNode) }
            }
        }.toTypedArray())
    }
}

@FeignClient(name = "tushare", url = "http://api.tushare.pro")
interface TushareFeignClient {
    @PostMapping
    fun <T> request(@RequestBody body: TushareRequestBody<T>): TushareResponseBody
}
