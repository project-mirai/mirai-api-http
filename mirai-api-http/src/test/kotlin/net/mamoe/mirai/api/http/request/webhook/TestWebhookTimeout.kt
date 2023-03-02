package net.mamoe.mirai.api.http.request.webhook

import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.coroutines.delay
import net.mamoe.mirai.Bot
import net.mamoe.mirai.api.http.adapter.http.router.respondDTO
import net.mamoe.mirai.api.http.adapter.internal.consts.Paths
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.LongTargetDTO
import net.mamoe.mirai.api.http.adapter.internal.serializer.toJsonElement
import net.mamoe.mirai.api.http.adapter.webhook.dto.WebhookPacket
import net.mamoe.mirai.api.http.request.startAdapter
import net.mamoe.mirai.api.http.setting.MainSetting
import net.mamoe.mirai.api.http.util.ExtendWith
import net.mamoe.mirai.api.http.util.SetupMockBot
import net.mamoe.mirai.console.data.findBackingFieldValue
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.mock.MockBot
import net.mamoe.mirai.mock.utils.broadcastMockEvents
import net.mamoe.yamlkt.Yaml
import net.mamoe.yamlkt.YamlElement
import kotlin.test.Test
import kotlin.test.assertFails

@ExtendWith(SetupMockBot::class)
class TestWebhookTimeout {

    private val verifyKey = "TestWebhookTimeout"

    @OptIn(ConsoleExperimentalApi::class)
    @Test
    fun testClientTimeout() {
        val serverPort = 9999

        // hijacked MainSettings
        val settings = MainSetting.findBackingFieldValue<Map<String, YamlElement>>("adapterSettings")
        val s = settings?.value as MutableMap
        s["webhook"] = Yaml.decodeYamlFromString(
            """
            destinations: ["http://localhost:9999/hook"]
        """.trimIndent()
        )


        // launch server
        embeddedServer(CIO, applicationEngineEnvironment {
            connector {
                host = "localhost"
                port = serverPort
            }

            modules.add {
                routing {
                    post("/hook") {
                        assertFails {
                            println("webhook server receive: ${call.receiveText()}")
                            delay(11_000)
                            call.respondDTO(
                                WebhookPacket(
                                    Paths.deleteFriend,
                                    LongTargetDTO(SetupMockBot.FRIEND_ID).toJsonElement()
                                )
                            )
                        }
                    }
                }
            }
        }).start(wait = false)

        // run adapter
        startAdapter(
            "webhook",
            verifyKey = verifyKey,
            enableVerify = true,
            singleMode = false,
            debug = true,
        ) {

            val bot = Bot.getInstance(SetupMockBot.ID) as MockBot

            broadcastMockEvents {
                bot.getFriend(SetupMockBot.FRIEND_ID)?.says("test")
            }

            delay(11_000)
        }
    }
}