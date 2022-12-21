package net.mamoe.mirai.api.http.ktor

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.ktor.utils.io.streams.*
import net.mamoe.mirai.api.http.adapter.http.router.file
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(io.ktor.utils.io.core.internal.DangerousInternalIoApi::class)
class TestMultiPartOriginalName {
    
    @Test
    fun readOriginalName() = withTestApplication(Application::main) { 
        with(handleRequest(HttpMethod.Post, "/test/readOriginalName") {
            val boundary = "readOriginalName"
            val file = File.createTempFile("Test", "").also { it.deleteOnExit() }

            addHeader(HttpHeaders.ContentType, ContentType.MultiPart.FormData.withParameter("boundary", boundary).toString())
            setBody(boundary, listOf(
                PartData.FileItem({ file.inputStream().asInput() }, {}, headersOf(
                    HttpHeaders.ContentDisposition,
                    ContentDisposition.Inline
                        .withParameter(ContentDisposition.Parameters.Name, "file")
                        .withParameter(ContentDisposition.Parameters.FileName, "测试UTF8字符")
                        .toString()
                ))
            ))  
        }) {
            assertEquals("测试UTF8字符", response.content)
        }
    }
}

private fun Application.main() {
    routing {
        post("/test/readOriginalName") {
            val parts = call.receiveMultipart().readAllParts()
            val file = parts.file("file")
            call.respond(file?.originalFileName ?: "")
        }
    }
}
