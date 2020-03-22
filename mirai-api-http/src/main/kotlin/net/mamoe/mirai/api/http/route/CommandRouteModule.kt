package net.mamoe.mirai.api.http.route

import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.close
import io.ktor.response.respondText
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.websocket.webSocket
import kotlinx.serialization.Serializable
import net.mamoe.mirai.api.http.HttpApiPluginBase
import net.mamoe.mirai.api.http.SessionManager
import net.mamoe.mirai.api.http.data.IllegalParamException
import net.mamoe.mirai.api.http.data.StateCode
import net.mamoe.mirai.api.http.data.common.DTO
import net.mamoe.mirai.api.http.util.toJson
import net.mamoe.mirai.console.MiraiConsole
import net.mamoe.mirai.console.command.AbstractCommandSender
import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.utils.managers
import net.mamoe.mirai.message.data.Message

fun Application.commandModule() {

    routing {
        miraiAuth<PostCommandDTO>("/command/register") {
            if (it.authKey != SessionManager.authKey) {
                call.respondStateCode(StateCode.AuthKeyFail)
            } else {
                HttpApiPluginBase.registerCommand(it.name, it.alias, it.description, it.usage)
                call.respondStateCode(StateCode.Success)
            }
        }

        miraiAuth<PostCommandDTO>("/command/send") {
            if (it.authKey != SessionManager.authKey) {
                call.respondStateCode(StateCode.AuthKeyFail)
            } else {
                CommandManager.runCommand(
                    sender = HttpCommandSender(call),
                    command = "${it.name} ${it.args.joinToString(" ")}"
                )
                call.respondStateCode(StateCode.Success)
            }
        }

        route("/managers", HttpMethod.Get) {
            intercept {
                val qq = call.parameters["qq"] ?: throw IllegalParamException("参数格式错误")
                val managers = getBotOrThrow(qq.toLong()).managers
                call.respondJson(managers.toJson())
            }
        }

        webSocket("/command") {
            // 校验Auth key
            val authKey = call.parameters["authKey"]
            if (authKey == null) {
                outgoing.send(Frame.Text(StateCode(400, "参数格式错误").toJson(StateCode.serializer())))
                close(CloseReason(CloseReason.Codes.NORMAL, "参数格式错误"))
                return@webSocket
            }
            if (authKey != SessionManager.authKey) {
                outgoing.send(Frame.Text(StateCode.AuthKeyFail.toJson(StateCode.serializer())))
                close(CloseReason(CloseReason.Codes.NORMAL, "Auth Key错误"))
                return@webSocket
            }

            // 订阅onCommand事件
            val subscriber = HttpApiPluginBase.subscribeCommand { name, args ->
                outgoing.send(Frame.Text(CommandDTO(name, args).toJson()))
            }

            try {
                // 阻塞websocket
                for (frame in incoming) {
                    /* do nothing */
                    HttpApiPluginBase.logger.info("command websocket send $frame")
                }
            } finally {
                HttpApiPluginBase.unSubscribeCommand(subscriber)
            }
        }
    }
}

class HttpCommandSender(private val call: ApplicationCall) : AbstractCommandSender() {
    override suspend fun sendMessage(message: String) {
        call.respondText(message)
    }

    override suspend fun sendMessage(messageChain: Message) {
        call.respondText(messageChain.toString())
    }
}

@Serializable
data class CommandDTO(
    val name: String,
    val args: List<String>
) : DTO

@Serializable
private data class PostCommandDTO(
    val authKey: String,
    val name: String,
    val alias: List<String> = emptyList(),
    val description: String = "",
    val usage: String = "",
    val args: List<String> = emptyList()
) : DTO
