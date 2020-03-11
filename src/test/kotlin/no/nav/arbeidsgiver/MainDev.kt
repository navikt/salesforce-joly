package no.nav.arbeidsgiver

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File
import java.net.URI
import java.util.*
import no.nav.arbeidsgiver.models.SfAccessToken
import no.nav.arbeidsgiver.models.SfdxListOrg
import no.nav.arbeidsgiver.sf.SalesforceClient
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory

fun main() {
    val log = LoggerFactory.getLogger("main")
    val s = File("./salesforce/tmp/JolyDev.json").readText()
    val mapper = ObjectMapper().registerKotlinModule()
    val data: SfdxListOrg = mapper.readValue(s)
    val instanceUrl = StringUtils.removeEnd(data.result.instanceUrl, "/")
    val accessToken = data.result.accessToken
    SalesforceClient.setToken(
        SfAccessToken(
            accessToken = accessToken,
            issuedAt = Calendar.getInstance().timeInMillis - 60 * 60,
            tokenType = "Bearer",
            id = URI(data.result.instanceUrl + "someid"),
            signature = "",
            instanceUrl = URI(instanceUrl)
        )
    )
    log.info("Access org: $instanceUrl/secur/frontdoor.jsp?sid=$accessToken")
    Bootstrap.start()
}
