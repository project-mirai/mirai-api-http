/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

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
import net.mamoe.mirai.Bot
import net.mamoe.mirai.api.http.HttpApiPluginBase
import net.mamoe.mirai.api.http.SessionManager
import net.mamoe.mirai.api.http.command.RegisterCommand
import net.mamoe.mirai.api.http.data.IllegalParamException
import net.mamoe.mirai.api.http.data.StateCode
import net.mamoe.mirai.api.http.data.common.DTO
import net.mamoe.mirai.api.http.util.toJson
import net.mamoe.mirai.console.command.CommandExecuteResult
import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.util.BotManager.INSTANCE.managers
import net.mamoe.mirai.console.util.ConsoleExperimentalAPI
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.message.MessageReceipt
import net.mamoe.mirai.message.data.Message
import org.jetbrains.annotations.Contract
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * 命令行路由
 */
fun Application.commandModule() {

    routing {
        /**
         * 注册命令
         */
        miraiAuth<PostCommandDTO>("/command/register") {
            if (it.authKey != SessionManager.authKey) {
                call.respondStateCode(StateCode.AuthKeyFail)
            } else {
                val names = ArrayList<String>(1 + it.alias.size).apply {
                    add(it.name)
                    addAll(it.alias)
                }

                RegisterCommand(it.description, it.usage, *names.toTypedArray()).register(true)
                call.respondStateCode(StateCode.Success)
            }
        }

        /**
         * 执行命令
         */
        miraiAuth<PostCommandDTO>("/command/send") {
            if (it.authKey != SessionManager.authKey) {
                call.respondStateCode(StateCode.AuthKeyFail)
            } else {
                val sender = HttpCommandSender(call)

                CommandManager.run {
                    val result = sender.executeCommand("${it.name} ${it.args.joinToString(" ")}")
                    when (result) {
                        is CommandExecuteResult.Success -> if (!sender.consume) call.respondText("")
                        else -> call.respondStateCode(StateCode.NoElement)
                    }
                }
            }
        }

        /**
         * 获取Manager
         */
        route("/managers", HttpMethod.Get) {
            intercept {
                val qq = call.parameters["qq"] ?: throw IllegalParamException("参数格式错误")
                val managers = getBotOrThrow(qq.toLong()).managers
                call.respondJson(managers.toJson())
            }
        }

        /**
         * 广播命令
         */
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
            val subscriber = HttpApiPluginBase.subscribeCommand { name, friend, group, args ->
                outgoing.send(Frame.Text(CommandDTO(name, friend, group, args).toJson()))
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

// TODO: 将command输出返回给请求
class HttpCommandSender(private val call: ApplicationCall, override val coroutineContext: CoroutineContext = EmptyCoroutineContext) : CommandSender {
    override val bot: Bot? = null
    override val name: String = "Mirai Http Api"
    override val subject: Contact? = null
    override val user: User? = null

    var consume = false

    override suspend fun sendMessage(message: String): MessageReceipt<Contact>? {
//        appendMessage(message)
        if (!consume) {
            call.respondText(message)
            consume = true
        }

        return null
    }

    override suspend fun sendMessage(messageChain: Message): MessageReceipt<Contact>? {
//        appendMessage(messageChain.toString())
        if (!consume) {
            call.respondText(messageChain.toString())
            consume = true
        }

        return null
    }

    @ConsoleExperimentalAPI
    override suspend fun catchExecutionException(e: Throwable) {
        // Nothing
    }


//    override suspend fun flushMessage() {
//        if (builder.isNotEmpty()) {
//            call.respondText(builder.toString().removeSuffix("\n"))
//        }
//    }
}

@Serializable
data class CommandDTO(
    val name: String,
    val friend: Long,
    val group: Long,
    val args: List<String>,
) : DTO

@Serializable
private data class PostCommandDTO(
    val authKey: String,
    val name: String,
    val alias: List<String> = emptyList(),
    val description: String = "",
    val usage: String = "",
    val args: List<String> = emptyList(),
) : DTO
