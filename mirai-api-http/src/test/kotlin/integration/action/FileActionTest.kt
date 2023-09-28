package integration.action

import framework.ExtendWith
import framework.SetupMockBot
import framework.testHttpApplication
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.utils.io.streams.*
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.consts.Paths
import net.mamoe.mirai.api.http.adapter.internal.dto.ElementResult
import net.mamoe.mirai.api.http.adapter.internal.dto.RemoteFileDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.*
import net.mamoe.mirai.api.http.adapter.internal.serializer.jsonElementParseOrNull
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestMethodOrder
import kotlin.test.*

@ExtendWith(SetupMockBot::class)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class FileActionTest {

    @Test
    @Order(1)
    fun testListFile() = testHttpApplication {
        client.get(Paths.httpPath(Paths.fileList)) {
            parameter("target", SetupMockBot.BEST_GROUP_ID)
            parameter("path", "/")
        }.body<RemoteFileList>().also {
            assertEquals(StateCode.Success.code, it.code)
            assertEquals(2, it.data.size)

            val (file, folder) = if (it.data[0].isFile) {
                it.data[0] to it.data[1]
            } else {
                it.data[1] to it.data[0]
            }

            assertTrue(folder.isDirectory)
            assertEquals("/tmpFolder", folder.path)
            assertEquals("tmpFolder", folder.name)
            assertNotNull(folder.parent)
            assertEquals("/", folder.parent.path)
            assertNull(folder.parent.parent)

            assertTrue(file.isFile)
            assertEquals("/test.txt", file.path)
            assertEquals("test.txt", file.name)
            assertNotNull(file.parent)
            assertEquals("/", file.parent.path)
            assertNull(file.parent.parent)
        }
    }

    @Test
    @Order(2)
    fun testFileInfo() = testHttpApplication {
        client.get(Paths.httpPath(Paths.fileInfo)) {
            parameter("target", SetupMockBot.BEST_GROUP_ID)
            parameter("path", "/test.txt")
            parameter("withDownloadInfo", true)
        }.body<ElementResult>().also {
            assertEquals(StateCode.Success.code, it.code)

            val data = it.data.jsonElementParseOrNull<RemoteFileDTO>()
            assertNotNull(data)
            assertEquals("/test.txt", data.path)
            assertEquals("test.txt", data.name)
            assertNotNull(data.parent)
            assertEquals("/", data.parent.path)
            assertNull(data.parent.parent)

            val download = data.downloadInfo
            assertNotNull(download)
            assertNotNull(download.url)
            assertNotNull(download.md5)
            assertNotNull(download.sha1)
        }

        client.get(Paths.httpPath(Paths.fileInfo)) {
            parameter("target", SetupMockBot.BEST_GROUP_ID)
            parameter("path", "/test.txt")
            parameter("withDownloadInfo", false)
        }.body<ElementResult>().also {
            assertEquals(StateCode.Success.code, it.code)

            val data = it.data.jsonElementParseOrNull<RemoteFileDTO>()
            assertNotNull(data)
            assertNull(data.downloadInfo)
        }

        client.get(Paths.httpPath(Paths.fileInfo)) {
            parameter("target", SetupMockBot.BEST_GROUP_ID)
            parameter("path", "/tmpFolder")
            parameter("withDownloadInfo", true)
        }.body<StateCode>().also {
            assertEquals(StateCode.NoElement.code, it.code)
        }
    }

    @Test
    @Order(3)
    fun testFileMkdir() = testHttpApplication {
        client.get(Paths.httpPath(Paths.fileList)) {
            parameter("target", SetupMockBot.BEST_GROUP_ID)
            parameter("path", "/mkdir")
        }.body<StateCode>().also {
            assertEquals(StateCode.NoElement.code, it.code)
        }

        postJsonData<ElementResult>(
            Paths.httpPath(Paths.fileMkdir), MkDirDTO(
                path = "/", target = SetupMockBot.BEST_GROUP_ID, directoryName = "mkdir"
            )
        ).also {
            assertEquals(StateCode.Success.code, it.code)

            val data = it.data.jsonElementParseOrNull<RemoteFileDTO>()
            assertNotNull(data)
            assertTrue(data.isDirectory)
            assertEquals("/mkdir", data.path)
            assertEquals("mkdir", data.name)
            assertNotNull(data.parent)
            assertEquals("/", data.parent.path)
        }

        client.get(Paths.httpPath(Paths.fileList)) {
            parameter("target", SetupMockBot.BEST_GROUP_ID)
            parameter("path", "/mkdir")
        }.body<RemoteFileList>().also {
            assertEquals(StateCode.Success.code, it.code)
            assertTrue(it.data.isEmpty())
        }
    }

    @Test
    @Order(4)
    fun testUploadFile() = testHttpApplication {
        client.get(Paths.httpPath(Paths.fileInfo)) {
            parameter("target", SetupMockBot.BEST_GROUP_ID)
            parameter("path", "/upload.txt")
        }.body<StateCode>().also {
            assertEquals(StateCode.NoElement.code, it.code)
        }

        client.submitFormWithBinaryData(Paths.httpPath(Paths.uploadFile), formData {
            append("path", "/")
            append("type", "group")
            append("target", SetupMockBot.BEST_GROUP_ID)
            append("file", "content", Headers.build {
                append(HttpHeaders.ContentDisposition, "filename=upload.txt")
            })
        }).body<ElementResult>().also {
            assertEquals(StateCode.Success.code, it.code)

            val data = it.data.jsonElementParseOrNull<RemoteFileDTO>()
            assertNotNull(data)
            assertTrue(data.isFile)
            assertEquals("/upload.txt", data.path)
            assertEquals("upload.txt", data.name)
        }

        client.get(Paths.httpPath(Paths.fileInfo)) {
            parameter("target", SetupMockBot.BEST_GROUP_ID)
            parameter("path", "/upload.txt")
        }.body<ElementResult>().also {
            assertEquals(StateCode.Success.code, it.code)

            val data = it.data.jsonElementParseOrNull<RemoteFileDTO>()
            assertNotNull(data)
        }
    }

    @Test
    @Order(5)
    fun testFileMove() = testHttpApplication {
        client.submitFormWithBinaryData(Paths.httpPath(Paths.uploadFile), formData {
            append("path", "/")
            append("type", "group")
            append("target", SetupMockBot.BEST_GROUP_ID)
            append("file", "content", Headers.build {
                append(HttpHeaders.ContentDisposition, "filename=move.txt")
            })
        }).body<ElementResult>().also {
            assertEquals(StateCode.Success.code, it.code)
        }

        postJsonData<StateCode>(
            Paths.httpPath(Paths.fileMove), MoveFileDTO(
                path = "/move.txt", target = SetupMockBot.BEST_GROUP_ID, moveToPath = "/noExist"
            )
        ).also {
            assertEquals(StateCode.NoElement.code, it.code)
        }

        postJsonData<StateCode>(
            Paths.httpPath(Paths.fileMove), MoveFileDTO(
                path = "/move.txt", target = SetupMockBot.BEST_GROUP_ID, moveToPath = "/tmpFolder"
            )
        ).also {
            assertEquals(StateCode.Success.code, it.code)
        }

        client.get(Paths.httpPath(Paths.fileInfo)) {
            parameter("target", SetupMockBot.BEST_GROUP_ID)
            parameter("path", "/tmpFolder/move.txt")
        }.body<ElementResult>().also {
            assertEquals(StateCode.Success.code, it.code)

            val data = it.data.jsonElementParseOrNull<RemoteFileDTO>()
            assertNotNull(data)
        }
    }

    @Test
    @Order(6)
    fun testFileRename() = testHttpApplication {
        client.submitFormWithBinaryData(Paths.httpPath(Paths.uploadFile), formData {
            append("path", "/")
            append("type", "group")
            append("target", SetupMockBot.BEST_GROUP_ID)
            append("file", "content", Headers.build {
                append(HttpHeaders.ContentDisposition, "filename=rename.txt")
            })
        }).body<ElementResult>().also {
            assertEquals(StateCode.Success.code, it.code)
        }

        postJsonData<StateCode>(
            Paths.httpPath(Paths.fileRename), RenameFileDTO(
                path = "/noExist.txt", group = SetupMockBot.BEST_GROUP_ID, renameTo = "newName.txt"
            )
        ).also {
            assertEquals(StateCode.NoElement.code, it.code)
        }

        postJsonData<StateCode>(
            Paths.httpPath(Paths.fileRename), RenameFileDTO(
                path = "/rename.txt", group = SetupMockBot.BEST_GROUP_ID, renameTo = "newName.txt"
            )
        ).also {
            assertEquals(StateCode.Success.code, it.code)
        }
    }

    @Test
    @Order(7)
    fun testFileDelete() = testHttpApplication {
        client.submitFormWithBinaryData(Paths.httpPath(Paths.uploadFile), formData {
            append("path", "/")
            append("type", "group")
            append("target", SetupMockBot.BEST_GROUP_ID)
            append("file", "content", Headers.build {
                append(HttpHeaders.ContentDisposition, "filename=delete.txt")
            })
        }).body<ElementResult>().also {
            assertEquals(StateCode.Success.code, it.code)
        }

        postJsonData<StateCode>(
            Paths.httpPath(Paths.fileDelete), FileTargetDTO(
                path = "/noExist.txt",
                group = SetupMockBot.BEST_GROUP_ID,
            )
        ).also {
            assertEquals(StateCode.NoElement.code, it.code)
        }

        postJsonData<StateCode>(
            Paths.httpPath(Paths.fileDelete), FileTargetDTO(
                path = "/delete.txt",
                group = SetupMockBot.BEST_GROUP_ID,
            )
        ).also {
            assertEquals(StateCode.Success.code, it.code)
        }
    }

    companion object {

        @JvmStatic
        @BeforeAll
        fun setupGroupFile(): Unit = runBlocking {
            SetupMockBot.instance().getGroupOrFail(SetupMockBot.BEST_GROUP_ID).files.apply {
                "content".byteInputStream().toExternalResource().use {
                    uploadNewFile("/test.txt", it)
                }


                root.createFolder("tmpFolder")
            }
        }
    }
}