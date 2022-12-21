/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package launch.httpserver

import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.*
import org.slf4j.helpers.NOPLogger

object EchoHttpServer {

    @JvmStatic
    fun main(args: Array<String>) {
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