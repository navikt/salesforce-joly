package no.nav.arbeidsgiver.sf

import org.cometd.client.transport.LongPollingTransport
import org.eclipse.jetty.client.HttpClient
import org.eclipse.jetty.client.api.Request
import org.eclipse.jetty.http.HttpHeader

open class SalesforcePollingTransport(options: Map<String, Any>, httpClient: HttpClient) :
    LongPollingTransport(options, httpClient) {
    private var token = ""
    override fun customize(request: Request) {
        super.customize(request)
        request.header(HttpHeader.AUTHORIZATION, token)
    }

    fun setToken(token: String) {
        this.token = token
    }
}
