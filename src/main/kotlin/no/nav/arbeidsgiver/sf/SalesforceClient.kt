package no.nav.arbeidsgiver.sf

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.net.URI
import java.util.*
import net.logstash.logback.argument.StructuredArguments
import no.nav.arbeidsgiver.EnvVar
import no.nav.arbeidsgiver.EnvVarFactory
import no.nav.arbeidsgiver.models.SfAccessToken
import org.apache.http.HttpHost
import org.apache.http.client.config.CookieSpecs
import org.apache.http.client.config.RequestConfig
import org.apache.http.impl.client.HttpClients
import org.http4k.client.ApacheClient
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.body.form
import org.slf4j.LoggerFactory

/**
 * https://github.com/navikt/ereg-sf/blob/master/src/main/kotlin/no/nav/ereg/SalesforceDSL.kt
 */
object SalesforceClient {
    private val log = LoggerFactory.getLogger(javaClass)
    private var tokenTimeout = 3600000; // One hour
    private var token: SfAccessToken? = null
    const val ENDPOINT_SOBJECTS = "/services/data/v47.0/composite/sobjects"
    const val ENDPOINT_TOKEN = "/services/oauth2/token"
    private const val ENDPOINT_QUERY = "/services/data/v20.0/query/"
    private const val ENDPOINT_ACCOUNT = "/services/data/v39.0/sobjects/Account/"
    private fun getToken(ev: EnvVar = EnvVarFactory.envVar): SfAccessToken {
        val endpointUri = ev.sfUrl + ENDPOINT_TOKEN
        log.info("Making request to: $endpointUri")
        val validRequest = Request(Method.POST, endpointUri)
            .form("grant_type", "password")
            .form("client_id", ev.sfClientId)
            .form("client_secret", ev.sfClientSecret)
            .form("username", ev.sfUsername)
            .form("password", ev.sfPassword + ev.sfUsertoken)
            .header("Content-Type", "application/x-www-form-urlencoded")
        val response = getHTTPClient()(validRequest)
        log.info("Received token with status ${response.status}")
        if (response.status !== Status.OK) {
            return mapToken(response.bodyString())
        } else {
            log.error("${response.status} - ${response.bodyString()}")
            throw Exception(response.bodyString())
        }
    }

    fun setToken(sfToken: SfAccessToken) {
        token = sfToken
    }

    fun getCachedToken(ev: EnvVar = EnvVarFactory.envVar): SfAccessToken {
        if (token === null || tokenIsOld(
                token!!
            )
        ) {
            token =
                getToken(ev)
        }
        return token as SfAccessToken
    }

    fun mapToken(tokenJson: String): SfAccessToken {
        val mapper = ObjectMapper().registerKotlinModule()
        mapper.propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
        val token: SfAccessToken = mapper.readValue(tokenJson)
        log.info("Token issued at: " + token.issuedAt)
        return token
    }

    private fun tokenIsOld(token: SfAccessToken): Boolean {
        val currentTime: Long = Calendar.getInstance().timeInMillis
        return (currentTime > token.issuedAt + tokenTimeout)
    }

    fun createAuthorizedRequest(method: Method, endpointPath: String): Request {
        val token = getCachedToken()
        val endpointUri = token.instanceUrl.toString() + endpointPath
        return Request(method, endpointUri)
            .header("Authorization", token.tokenType + " " + token.accessToken)
            .header("Content-Type", "application/json;charset=UTF-8")
    }

    fun getHTTPClient(httpsProxy: String = EnvVarFactory.envVar.httpsProxy) =
        if (httpsProxy.isNotEmpty())
            ApacheClient(
                client = HttpClients.custom()
                    .setDefaultRequestConfig(
                        RequestConfig.custom()
                            .setProxy(HttpHost(URI(httpsProxy).host, URI(httpsProxy).port, URI(httpsProxy).scheme))
                            .setRedirectsEnabled(false)
                            .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                            .build()
                    )
                    .build()
            ) else ApacheClient()

    // curl https://yourInstance.salesforce.com/services/data/v20.0/query/?q=SELECT+name+from+Account -H "Authorization: Bearer token"
    fun querySalesforce(sosql: String): String {
        val request = createAuthorizedRequest(
            Method.GET,
            ENDPOINT_QUERY
        )
            .query("q", sosql)
        val response = getHTTPClient()(request)
        requestLogger(
            ENDPOINT_QUERY,
            response
        )
        return response.bodyString()
    }

    fun requestLogger(endpointPath: String, response: Response) {
        if (response.status !== Status.OK) {
            log.info(
                "Status " + response.status + ", length: " + response.bodyString().length,
                StructuredArguments.value("body", response.bodyString()),
                StructuredArguments.value("endpoint", endpointPath)
            )
        } else {
            log.warn(
                "Status " + response.status + ", length: " + response.bodyString().length,
                StructuredArguments.value("body", response.bodyString()),
                StructuredArguments.value("endpoint", endpointPath)
            )
        }
    }
}
