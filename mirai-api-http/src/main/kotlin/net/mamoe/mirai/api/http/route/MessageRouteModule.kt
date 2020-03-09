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
import io.ktor.application.call
import io.ktor.http.content.streamProvider
import io.ktor.routing.routing
import kotlinx.serialization.Serializable
import net.mamoe.mirai.api.http.data.IllegalAccessException
import net.mamoe.mirai.api.http.data.IllegalParamException
import net.mamoe.mirai.api.http.data.StateCode
import net.mamoe.mirai.api.http.data.common.*
import net.mamoe.mirai.api.http.util.toJson
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.getFriendOrNull
import net.mamoe.mirai.message.*
import net.mamoe.mirai.message.data.*
import java.net.URL

fun Application.messageModule() {
    routing {

        miraiGet("/fetchMessage") {
            val count: Int = paramOrNull("count")
            val fetch = it.messageQueue.fetch(count)

            call.respondJson(fetch.toJson())
        }

        miraiGet("/messageFromId") {
            val id: Long = paramOrNull("id")
            it.cacheQueue[id].apply {
                call.respondDTO(this.toDTO())
            }
        }

        suspend fun <C : Contact> sendMessage(
            quote: QuoteReplyToSend?,
            messageChain: MessageChain,
            target: C
        ): MessageReceipt<out Contact> {
            val send = if (quote == null) {
                messageChain
            } else {
                ((quote + messageChain) as Iterable<Message>).asMessageChain()
            }
            return target.sendMessage(send)
        }

        miraiVerify<SendDTO>("/sendFriendMessage") {
            val quote = it.quote?.let { q ->
                it.session.cacheQueue[q].run {
                    this[MessageSource].quote(sender)
                }
            }

            it.session.bot.getFriend(it.target).apply {
                val receipt = sendMessage(quote, it.messageChain.toMessageChain(this), this)
                receipt.source.ensureSequenceIdAvailable()

                it.session.cacheQueue.add(FriendMessage(bot.selfQQ, receipt.source.asMessageChain()))
                call.respondDTO(SendRetDTO(messageId = receipt.source.id))
            }
        }

        miraiVerify<SendDTO>("/sendGroupMessage") {
            val quote = it.quote?.let { q ->
                it.session.cacheQueue[q].run {
                    this[MessageSource].quote(sender)
                }
            }

            it.session.bot.getGroup(it.target).apply {
                val receipt = sendMessage(quote, it.messageChain.toMessageChain(this), this)
                receipt.source.ensureSequenceIdAvailable()

                it.session.cacheQueue.add(
                    GroupMessage(
                        "",
                        botPermission,
                        botAsMember,
                        receipt.source.asMessageChain()
                    )
                )
                call.respondDTO(SendRetDTO(messageId = receipt.source.id))
            }
        }

        miraiVerify<SendImageDTO>("sendImageMessage") {
            val bot = it.session.bot
            val contact = when {
                it.target != null -> bot.getFriendOrNull(it.target) ?: bot.getGroup(it.target)
                it.qq != null -> bot.getFriend(it.qq)
                it.group != null -> bot.getGroup(it.group)
                else -> throw IllegalParamException("target、qq、group不可全为null")
            }
            val ls = it.urls.map { url -> contact.uploadImage(URL(url)) }
            contact.sendMessage(buildMessageChain { addAll(ls) })
            call.respondJson(ls.map { image -> image.imageId }.toJson())
        }

        // TODO: 重构
        miraiMultiPart("uploadImage") { session, parts ->

            val type = parts.value("type")
            parts.file("img")?.apply {
                val image = streamProvider().use {
                    when (type) {
                        "group" -> session.bot.groups.firstOrNull()?.uploadImage(it)
                        "friend" -> session.bot.friends.firstOrNull()?.uploadImage(it)
                        else -> null
                    }
                }
                image?.apply {
                    call.respondDTO(UploadImageRetDTO(imageId, trickImageUrl(this, type)))
                } ?: throw IllegalAccessException("图片上传错误")
            } ?: throw IllegalAccessException("未知错误")
        }

        miraiVerify<RecallDTO>("recall") {
            it.session.cacheQueue[it.target].apply {
                it.session.bot.recall(get(MessageSource))
            }
            call.respondStateCode(StateCode.Success)
        }
    }
}

@Serializable
private data class SendDTO(
    override val sessionKey: String,
    val quote: Long? = null,
    val target: Long,
    val messageChain: MessageChainDTO
) : VerifyDTO()

@Serializable
private data class SendImageDTO(
    override val sessionKey: String,
    val target: Long? = null,
    val qq: Long? = null,
    val group: Long? = null,
    val urls: List<String>
) : VerifyDTO()

@Serializable
@Suppress("unused")
private class SendRetDTO(
    val code: Int = 0,
    val msg: String = "success",
    val messageId: Long
) : DTO

@Serializable
@Suppress("unused")
private class UploadImageRetDTO(
    val imageId: String,
    val url: String
) : DTO

@Serializable
private data class RecallDTO(
    override val sessionKey: String,
    val target: Long
) : VerifyDTO()

private fun trickImageUrl(image: Image, type: String) = when (type) {
    "friend" -> "http://c2cpicdw.qpic.cn/offpic_new/0/${image.imageId}/0"
    "group" -> "http://gchat.qpic.cn/gchatpic_new/0/0-0-${image.imageId.parseGId()}/0"
    else -> "未知"
}

private fun String.parseGId() =
    substringAfter('{').substringBefore('}').replace("-", "").toUpperCase()
