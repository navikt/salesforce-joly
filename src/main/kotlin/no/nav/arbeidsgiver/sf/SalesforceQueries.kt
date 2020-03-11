package no.nav.arbeidsgiver.sf

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import no.nav.arbeidsgiver.Utils
import no.nav.arbeidsgiver.models.SfTask
import org.http4k.core.Method

object SalesforceQueries {
    private const val ENDPOINT_TASK = "/services/data/v48.0/sobjects/Task/"

    fun getTask(id: String): SfTask {
        val request = SalesforceClient.createAuthorizedRequest(Method.GET, ENDPOINT_TASK + id)
        val response = SalesforceClient.getHTTPClient()(request)
        SalesforceClient.requestLogger(ENDPOINT_TASK, response)
        val resultString = response.bodyString()
        val mapper = ObjectMapper().registerKotlinModule()
        return mapper.readValue(resultString)
    }

    fun patchTask(id: String, patch: Any): String {
        val jsonString = Utils.toJson(patch)
        val request = SalesforceClient
            .createAuthorizedRequest(Method.PATCH, ENDPOINT_TASK + id)
            .body(jsonString)
        val response = SalesforceClient.getHTTPClient()(request)
        SalesforceClient.requestLogger(ENDPOINT_TASK, response)
        return response.bodyString()
    }
}
