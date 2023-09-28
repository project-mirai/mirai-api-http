package net.mamoe.mirai.api.http.adapter.http

import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.ktor.util.*
import io.ktor.utils.io.*
import io.ktor.utils.io.streams.*
import net.mamoe.mirai.api.http.adapter.http.router.file
import kotlin.test.Test
import kotlin.test.assertEquals

class TestMultiPartOriginalName {

    @Test
    @OptIn(InternalAPI::class)
    fun readOriginalNameWithUTF8() = testApplication {
        routing {
            post("/test/readOriginalName") {
                val parts = call.receiveMultipart().readAllParts()
                val file = parts.file("file")
                call.respond(file?.originalFileName ?: "")
            }
        }

        val filename = "测试UTF8字符"

        client.post("/test/readOriginalName") {
            val boundary = "readOriginalNameWithUTF8"

            setBody(MultiPartFormDataContent(
                listOf(
                    PartData.FileItem({ "".byteInputStream().asInput() }, {}, headersOf(
                        HttpHeaders.ContentDisposition,
                        ContentDisposition.Inline
                            .withParameter(ContentDisposition.Parameters.Name, "file")
                            .withParameter(ContentDisposition.Parameters.FileName, filename)
                            .toString()
                    ))
                ),
                boundary,
                ContentType.MultiPart.FormData.withParameter("boundary", boundary)
            ))
        }.apply {
            assertEquals(filename, content.readUTF8Line())
        }
    }
}
