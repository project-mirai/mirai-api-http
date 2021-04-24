/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.http.router
//
//import io.ktor.application.*
//import io.ktor.http.*
//import io.ktor.http.cio.websocket.*
//import io.ktor.response.*
//import io.ktor.routing.*
//import io.ktor.websocket.*
//import kotlinx.serialization.Serializable
//import net.mamoe.mirai.Bot
//import net.mamoe.mirai.api.http.HttpApiPluginBase
//import net.mamoe.mirai.api.http.adapter.http.router.handleException
//import net.mamoe.mirai.api.http.adapter.http.router.httpAuth
//import net.mamoe.mirai.api.http.adapter.common.IllegalParamException
//import net.mamoe.mirai.api.http.adapter.internal.dto.DTO
//import net.mamoe.mirai.api.http.util.toJson
//import net.mamoe.mirai.console.command.CommandSender
//import net.mamoe.mirai.console.permission.AbstractPermitteeId
//import net.mamoe.mirai.console.permission.PermitteeId
//import net.mamoe.mirai.console.util.ConsoleExperimentalApi
//import net.mamoe.mirai.contact.Contact
//import net.mamoe.mirai.contact.User
//import net.mamoe.mirai.message.MessageReceipt
//import net.mamoe.mirai.message.data.Message
//import kotlin.coroutines.CoroutineContext
//import kotlin.coroutines.EmptyCoroutineContext
//
///**
// * 命令行路由
// */
//@OptIn(ConsoleExperimentalApi::class)
//fun Application.commandModule() {
//
//    routing {
//        /**
//         * 注册命令
//         */
//        httpAuth<PostCommandDTO>("/command/register") {
////            if (it.authKey != SessionManager.authKey) {
////                call.respondStateCode(StateCode.AuthKeyFail)
////            } else {
////                val names = ArrayList<String>(1 + it.alias.size).apply {
////                    add(it.name)
////                    addAll(it.alias)
////                }
////
//////                RegisterCommand(it.description, it.usage, *names.toTypedArray()).register(true)
////                call.respondStateCode(StateCode(-1, "未支持操作"))
////            }
//        }
//
//        /**
//         * 执行命令
//         */
//        httpAuth<PostCommandDTO>("/command/send") {
////            if (it.authKey != SessionManager.authKey) {
////                call.respondStateCode(StateCode.AuthKeyFail)
////            } else {
////                val sender = HttpCommandSender(call)
////
////                CommandManager.run {
////                    when (val result = executeCommand(sender, PlainText("${it.name} ${it.args.joinToString(" ")}"))) {
////                        is CommandExecuteResult.Success -> if (!sender.consume) call.respondText("")
////                        else -> call.respondStateCode(StateCode.NoElement)
////                    }
////                }
////            }
//        }
//
//        /**
//         * 获取Manager
//         */
//        route("/managers", HttpMethod.Get) {
//            handleException {
//                val qq = call.parameters["qq"] ?: throw IllegalParamException("参数格式错误")
//                val managers = listOf<Long>()
//                call.respondJson(managers.toJson())
//            }
//        }
//
//        /**
//         * 广播命令
//         */
//        webSocket("/command") {
//            // 校验Auth key
//            val authKey = call.parameters["authKey"]
////            if (authKey == null) {
////                outgoing.send(Frame.Text(StateCode(400, "参数格式错误").toJson(StateCode.serializer())))
////                close(CloseReason(CloseReason.Codes.NORMAL, "参数格式错误"))
////                return@webSocket
////            }
////            if (authKey != SessionManager.authKey) {
////                outgoing.send(Frame.Text(StateCode.AuthKeyFail.toJson(StateCode.serializer())))
////                close(CloseReason(CloseReason.Codes.NORMAL, "Auth Key错误"))
////                return@webSocket
////            }
//
//            // 订阅onCommand事件
//            val subscriber = HttpApiPluginBase.subscribeCommand { name, friend, group, args ->
//                outgoing.send(Frame.Text(CommandDTO(name, friend, group, args).toJson()))
//            }
//
//            try {
//                // 阻塞websocket
//                for (frame in incoming) {
//                    /* do nothing */
//                    HttpApiPluginBase.logger.info("command websocket send $frame")
//                }
//            } finally {
//                HttpApiPluginBase.unSubscribeCommand(subscriber)
//            }
//        }
//    }
//}
//
//// TODO: 将command输出返回给请求
//class HttpCommandSender(
//    private val call: ApplicationCall,
//    override val coroutineContext: CoroutineContext = EmptyCoroutineContext
//) : CommandSender {
//    override val bot: Bot? = null
//    override val name: String = "Mirai Http Api"
//    override val permitteeId: PermitteeId
//        get() = object : PermitteeId {
//            override val directParents: Array<out PermitteeId>
//                get() = arrayOf(AbstractPermitteeId.Console)
//
//            override fun asString(): String = "http-api"
//        }
//
//
//    override val subject: Contact? = null
//    override val user: User? = null
//
//    var consume = false
//
//    override suspend fun sendMessage(message: String): MessageReceipt<Contact>? {
////        appendMessage(message)
//        if (!consume) {
//            call.respondText(message)
//            consume = true
//        }
//
//        return null
//    }
//
//    override suspend fun sendMessage(message: Message): MessageReceipt<Contact>? {
////        appendMessage(messageChain.toString())
//        if (!consume) {
//            call.respondText(message.toString())
//            consume = true
//        }
//
//        return null
//    }
//
//    /*override suspend fun catchExecutionException(e: Throwable) {
//        // Nothing
//    }*/
//
//
////    override suspend fun flushMessage() {
////        if (builder.isNotEmpty()) {
////            call.respondText(builder.toString().removeSuffix("\n"))
////        }
////    }
//}
//
//@Serializable
//data class CommandDTO(
//    val name: String,
//    val friend: Long,
//    val group: Long,
//    val args: List<String>,
//) : DTO
//
//@Serializable
//private data class PostCommandDTO(
//    val authKey: String,
//    val name: String,
//    val alias: List<String> = emptyList(),
//    val description: String = "",
//    val usage: String = "",
//    val args: List<String> = emptyList(),
//) : DTO
