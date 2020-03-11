package no.nav.arbeidsgiver.sf

import java.net.URI
import no.nav.arbeidsgiver.EnvVarFactory
import org.cometd.client.BayeuxClient
import org.eclipse.jetty.client.HttpClient
import org.eclipse.jetty.client.HttpProxy
import org.eclipse.jetty.util.ssl.SslContextFactory
import org.slf4j.LoggerFactory

object SalesforceBayeux {
    private val log = LoggerFactory.getLogger(javaClass)
    private var client: BayeuxClient? = null
    private const val ENDPOINT_COMETD = "/cometd/48.0"
    fun getClient(httpsProxy: String = EnvVarFactory.envVar.httpsProxy): BayeuxClient {
        if (client === null) {
            val token = SalesforceClient.getCachedToken()
            val options: Map<String, Any> = HashMap()
            val httpClient = HttpClient(SslContextFactory.Client())
            if (httpsProxy.isNotEmpty()) {
                httpClient.proxyConfiguration.proxies.add(HttpProxy(URI(httpsProxy).host, URI(httpsProxy).port))
            }
            httpClient.start()
            val transport = SalesforcePollingTransport(options, httpClient)
            val url = token.instanceUrl.toString() + ENDPOINT_COMETD
            transport.setToken(token.tokenType + " " + token.accessToken)
            val bayeuxClient = BayeuxClient(url, transport)
            bayeuxClient.setAttribute("debug", true)
            log.info("Trying to handshake: $url")
            bayeuxClient.handshake()
            val handshaken: Boolean = bayeuxClient.waitFor(5 * 1000, BayeuxClient.State.CONNECTED)
            if (!handshaken) {
                log.error("Failed to handshake: $bayeuxClient")
                bayeuxClient.disconnect()
            }
            client = bayeuxClient
        }
        return client as BayeuxClient
    }
}
