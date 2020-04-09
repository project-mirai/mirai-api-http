package net.mamoe.mirai.api.http.util

import io.ktor.http.HttpMethod
import io.ktor.client.request.*
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.ContentType
import io.ktor.http.content.PartData
import io.ktor.http.content.TextContent
import io.ktor.util.InternalAPI

/**
 * HTTP请求客户端
 */
class HttpClient {
    companion object {
        /**
         * 使用Ktor的HttpClient
         */
        private val client = io.ktor.client.HttpClient()

        /**
         * POST请求 (String)
         */
        suspend fun post(path: String, json: String, headerMap: Map<String, Any>): String {
            return client.request {
                url(path)
                headers {
                    headerMap.forEach { (k, v) -> append(k, v.toString()) }
                }
                method = HttpMethod.Post
                body = TextContent(json, ContentType.Application.Json)
            }
        }

        /**
         * POST请求（PartData）
         */
        suspend fun post(path: String, contentMap: Map<String, Any>, headerMap: Map<String, Any>): String {
            return client.request {
                url(path)
                method = HttpMethod.Post
                headers {
                    headerMap.forEach { (k, v) -> append(k, v.toString()) }
                }
                body = MultiPartFormDataContent(convertMapToPartDataList(contentMap))
            }
        }

        /**
         * POST请求（PartData）
         */
        suspend fun post(path: String, content: List<PartData>): String {
            return client.request {
                url(path)
                method = HttpMethod.Post
                body = MultiPartFormDataContent(content)
            }
        }

        @OptIn(InternalAPI::class)
        fun convertMapToPartDataList(contentMap: Map<String, Any>): List<PartData> {
            return formData {
                contentMap.forEach { (key, value) -> append(key, value) }
            }
        }
    }
}
