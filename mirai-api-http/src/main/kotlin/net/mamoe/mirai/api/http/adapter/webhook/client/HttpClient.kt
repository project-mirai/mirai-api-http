/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.webhook.client

import io.ktor.client.*
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.content.PartData
import io.ktor.http.content.TextContent
import io.ktor.util.InternalAPI

class WebhookHttpClient(private val headers: Map<String, String>) {

    /**
     * 使用 Ktor 的 [HttpClient]
     */
    private val client = HttpClient()

    /**
     * POST请求 (String)
     */
    suspend fun post(path: String, content: String, botId: Long? = null): String {
        return client.request {
            url(path)
            headers {
                this@WebhookHttpClient.headers.forEach { (k, v) -> append(k, v) }
                botId?.let { append("bot", it.toString()) }
            }
            method = HttpMethod.Post
            body = TextContent(content, ContentType.Application.Json)
        }
    }
}
