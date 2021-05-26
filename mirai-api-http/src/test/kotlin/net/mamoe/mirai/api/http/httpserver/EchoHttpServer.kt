package net.mamoe.mirai.api.http.httpserver

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.util.*
import org.junit.Test
import org.slf4j.helpers.NOPLogger

class EchoHttpServer {

    @Test
    fun launchTestServer() {
        embeddedServer(CIO, applicationEngineEnvironment {

            log = NOPLogger.NOP_LOGGER

            module {
                routing {
                    post("/") {
                        val receiveText = call.receiveText()
                        call.respondText(receiveText, call.defaultTextContentType(ContentType.Application.Json))
                        println(receiveText)
                        println(call.request.headers.toMap())
                        println("===================")
                    }
                }
            }

            connector {
                host = "localhost"
                port = 9999
            }
        }).start(true)
    }
}