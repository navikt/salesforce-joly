package no.nav.arbeidsgiver

import com.fasterxml.jackson.databind.ObjectMapper
import org.http4k.core.*
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Http4kServer
import org.http4k.server.Netty
import org.http4k.server.asServer
import org.slf4j.LoggerFactory

object Server {
    private const val INDEX = "/"
    private const val ISALIVE = "/its-alive"
    private const val ISREADY = "/its-ready"

    private val log = LoggerFactory.getLogger(javaClass)
    fun create(envVar: EnvVar): Http4kServer {
        val inLocalDev = envVar.naisClusterName === Const.LOCALDEV
        val inDev = envVar.naisClusterName === Const.DEV_FSS || inLocalDev

        val indexHandler: HttpHandler = { _ ->
            var url = ""
            if (inLocalDev) {
                url = "http://localhost:" + envVar.port
            }
            val data = mapOf(
                "INDEX" to url + INDEX,
                "ISALIVE" to url + ISALIVE,
                "ISREADY" to url + ISREADY
            )
            val json = ObjectMapper().writeValueAsString(data)
            conditionalResponse(inDev, json)
        }

        if (envVar.naisClusterName === Const.LOCALDEV) {
            log.info("Starting server http://localhost:" + envVar.port)
        }
        return routes(
            INDEX bind Method.GET to indexHandler,
            ISALIVE bind Method.GET to { Response(Status.OK).body("its alive") },
            ISREADY bind Method.GET to { Response(Status.OK).body("its ready") }
        ).asServer(Netty(envVar.port)).start()
    }

    private fun conditionalResponse(inDev: Boolean, data: String): Response {
        return if (inDev) {
            Response(Status.OK).body(data)
        } else {
            Response(Status.OK).body("Debug endepunkt: " + data.length)
        }
    }
}
