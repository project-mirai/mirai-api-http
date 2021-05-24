/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.route

import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.Serializable
import net.mamoe.mirai.api.http.AuthedSession
import net.mamoe.mirai.api.http.HttpApiPluginBase
import net.mamoe.mirai.api.http.data.IllegalAccessException
import net.mamoe.mirai.api.http.data.IllegalParamException
import net.mamoe.mirai.api.http.data.StateCode
import net.mamoe.mirai.api.http.data.common.*
import net.mamoe.mirai.api.http.generateSessionKey
import net.mamoe.mirai.api.http.util.toJson
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Contact.Companion.uploadImage
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.MessageReceipt
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.message.data.MessageSource.Key.recall
import net.mamoe.mirai.message.data.OnlineMessageSource.Incoming
import net.mamoe.mirai.message.data.OnlineMessageSource.Outgoing
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import java.net.URL

/**
 * 消息路由
 */
fun Application.messageModule() {
    routing {

        /**
         * 获取MessageQueue剩余消息数量
         */
        miraiGet("/countMessage") {
            val count: Int = it.messageQueue.size

            call.respondDTO(IntRestfulResult(data = count))
        }

        /**
         * 获取指定条数最老的消息并从MessageQueue删除获取的消息
         */
        miraiGet("/fetchMessage") {
            val count: Int = paramOrNull("count")
            val list = it.messageQueue.fetch(count)

            call.respondDTO(EventListRestfulResult(data = list))
        }

        /**
         * 获取指定条数最新的消息并从MessageQueue删除获取的消息
         */
        miraiGet("/fetchLatestMessage") {
            val count: Int = paramOrNull("count")
            val list = it.messageQueue.fetchLatest(count)

            call.respondDTO(EventListRestfulResult(data = list))
        }

        /**
         * 获取指定条数最老的消息，和/fetchMessage不一样，这个方法不会删除消息
         */
        miraiGet("/peekMessage") {
            val count: Int = paramOrNull("count")
            val list = it.messageQueue.peek(count)

            call.respondDTO(EventListRestfulResult(data = list))
        }

        /**
         * 获取指定条数最新的消息，和/fetchLatestMessage不一样，这个方法不会删除消息
         */
        miraiGet("/peekLatestMessage") {
            val count: Int = paramOrNull("count")
            val list = it.messageQueue.peekLatest(count)

            call.respondDTO(EventListRestfulResult(data = list))
        }

        /**
         * 获取指定ID消息（从CacheQueue获取）
         */
        miraiGet("/messageFromId") {
            val id: Int = paramOrNull("id")
            it.cacheQueue[id].apply {

                val dto = when (this) {
                    is Outgoing.ToGroup -> GroupMessagePacketDTO(MemberDTO(target.botAsMember))
                    is Outgoing.ToFriend -> FriendMessagePacketDTO(QQDTO(sender.asFriend))
                    is Outgoing.ToTemp -> TempMessagePacketDto(MemberDTO(target))
                    is Outgoing.ToStranger -> StrangerMessagePacketDto(QQDTO(target))

                    is Incoming.FromGroup -> GroupMessagePacketDTO(MemberDTO(sender))
                    is Incoming.FromFriend -> FriendMessagePacketDTO(QQDTO(sender))
                    is Incoming.FromTemp -> TempMessagePacketDto(MemberDTO(sender))
                    is Incoming.FromStranger -> StrangerMessagePacketDto(QQDTO(sender))
                }

                dto.messageChain = messageChainOf(this, originalMessage)
                    .toMessageChainDTO { d -> d != UnknownMessageDTO }
                call.respondDTO(
                    EventRestfulResult(
                        data = dto
                    )
                )
            }
        }

        /**
         * 发送消息
         */
        suspend fun <C : Contact> sendMessage(
            quote: QuoteReply?,
            messageChain: MessageChain,
            target: C
        ): MessageReceipt<Contact> {
            val send = if (quote == null) {
                messageChain
            } else {
                ((quote + messageChain) as Iterable<Message>).toMessageChain()
            }
            return target.sendMessage(send)
        }

        /**
         * 发送消息给好友
         */
        miraiVerify<SendDTO>("/sendFriendMessage") {
            val quote = it.quote?.let { q ->
                it.session.cacheQueue[q].run {
                    this.quote()
                }
            }

            val bot = it.session.bot

            fun findQQ(qq: Long): Contact = bot.getFriend(qq)
                ?: bot.getStranger(qq)
                ?: throw NoSuchElementException("friend $qq not found")

            val qq = when {
                it.target != null -> findQQ(it.target)
                it.qq != null -> findQQ(it.qq)
                else -> throw NoSuchElementException()
            }

            val receipt = sendMessage(quote, it.messageChain.toMessageChain(qq), qq)
            it.session.cacheQueue.add(receipt.source)

            call.respondDTO(SendRetDTO(messageId = receipt.source.ids.firstOrNull() ?: 0))
        }

        /**
         * 发送消息到QQ群
         */
        miraiVerify<SendDTO>("/sendGroupMessage") {
            val quote = it.quote?.let { q ->
                it.session.cacheQueue[q].run {
                    this.quote()
                }
            }

            val bot = it.session.bot
            val group = when {
                it.target != null -> bot.getGroupOrFail(it.target)
                it.group != null -> bot.getGroupOrFail(it.group)
                else -> throw NoSuchElementException()
            }

            val receipt = sendMessage(quote, it.messageChain.toMessageChain(group), group)
            it.session.cacheQueue.add(receipt.source)

            call.respondDTO(SendRetDTO(messageId = receipt.source.ids.firstOrNull() ?: 0))
        }

        /**
         * 发送消息给临时会话
         */
        miraiVerify<SendDTO>("/sendTempMessage") {
            val quote = it.quote?.let { q ->
                it.session.cacheQueue[q].run {
                    this.quote()
                }
            }

            val bot = it.session.bot
            val member = when {
                it.qq != null && it.group != null -> bot.getGroupOrFail(it.group).getOrFail(it.qq)
                else -> throw NoSuchElementException()
            }

            val receipt = sendMessage(quote, it.messageChain.toMessageChain(member), member)
            it.session.cacheQueue.add(receipt.source)

            call.respondDTO(SendRetDTO(messageId = receipt.source.ids.firstOrNull() ?: 0))
        }

        /**
         * 发送图片消息
         */
        miraiVerify<SendImageDTO>("sendImageMessage") { dto ->
            val bot = dto.session.bot
            val contact = when {
                dto.target != null -> bot.getFriend(dto.target) ?: bot.getGroupOrFail(dto.target)
                dto.qq != null && dto.group != null -> bot.getGroupOrFail(dto.group).getOrFail(dto.qq)
                dto.qq != null -> bot.getFriendOrFail(dto.qq)
                dto.group != null -> bot.getGroupOrFail(dto.group)
                else -> throw IllegalParamException("target、qq、group不可全为null")
            }
            val ls = dto.urls.map { url -> URL(url).openStream().use { it.uploadAsImage(contact) } }
            val receipt = contact.sendMessage(buildMessageChain { addAll(ls) })

            dto.session.cacheQueue.add(receipt.source)
            call.respondJson(ls.map { image -> image.imageId }.toJson())
        }


        // TODO: 重构
        miraiMultiPart("uploadImage") { session, parts ->

            var path: String?

            val type = parts.value("type")
            parts.file("img")?.apply {

                val image = streamProvider().use {
                    // originalFileName assert not null
                    val newFile = HttpApiPluginBase.saveImageAsync(
                        originalFileName ?: generateSessionKey(), it.readBytes()
                    )

                    when (type) {
                        "group" -> session.bot.groups.firstOrNull()?.uploadImage(newFile.await())
                        "friend",
                        "temp"
                        -> session.bot.friends.firstOrNull()?.uploadImage(newFile.await())
                        else -> null
                    }.apply {
                        // 使用apply不影响when返回
                        path = newFile.await().absolutePath
                    }
                }

                image?.apply {
                    call.respondDTO(
                        UploadImageRetDTO(
                            imageId,
                            queryUrl(),
                            path
                        )
                    )
                } ?: throw IllegalAccessException("图片上传错误")

            } ?: throw IllegalAccessException("未知错误")
        }

        miraiMultiPart("uploadVoice") { session, parts ->

            var path: String?

            val type = parts.value("type")
            parts.file("voice")?.apply {

                val voice = streamProvider().use { inputStream ->
                    // originalFileName assert not null
                    val newFile = HttpApiPluginBase.saveVoiceAsync(
                        originalFileName ?: generateSessionKey(), inputStream.readBytes()
                    )

                    when (type) {
                        "group" -> {
                            newFile.await().toExternalResource().use {
                                session.bot.groups.firstOrNull()?.uploadVoice(it)
                            }
                        }
                        else -> null
                    }.apply {
                        // 使用apply不影响when返回
                        path = newFile.await().absolutePath

                    }
                }

                voice?.apply {
                    call.respondDTO(
                        UploadVoiceRetDTO(
                            fileName,
                            url,
                            path
                        )
                    )
                } ?: throw IllegalAccessException("语音上传错误")

            } ?: throw IllegalAccessException("未知错误")
        }

        /**
         * 撤回消息
         */
        miraiVerify<RecallDTO>("recall") {
            it.session.cacheQueue[it.target].recall()
            call.respondStateCode(StateCode.Success)
        }

        /**
         * 设置群精华消息
         */
        miraiVerify<EssenceDTO>("/setEssence") { dto ->
            val source = dto.session.cacheQueue[dto.target]
            val group: Group = dto.session.bot.getGroupOrFail(source.target.id)
            val success = group.setEssenceMessage(source)
            call.respondStateCode(
                if (success) StateCode.Success
                else StateCode.PermissionDenied
            )
        }
    }
}

@Serializable
private data class SendDTO(
    override val sessionKey: String,
    val quote: Int? = null,
    val target: Long? = null,
    val qq: Long? = null,
    val group: Long? = null,
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
    val messageId: Int
) : DTO

@Serializable
@Suppress("unused")
private class UploadImageRetDTO(
    val imageId: String,
    val url: String,
    val path: String?
) : DTO

@Serializable
@Suppress("unused")
private class UploadVoiceRetDTO(
    val voiceId: String,
    val url: String?,
    val path: String?
) : DTO

@Serializable
private data class EssenceDTO(
    override val sessionKey: String,
    val target: Int
) : VerifyDTO()

@Serializable
private data class RecallDTO(
    override val sessionKey: String,
    val target: Int
) : VerifyDTO()