/*
 * Copyright 2023 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package integration.ktor

import framework.testWebsocketApplication
import io.ktor.client.request.forms.*
import io.ktor.http.*
import net.mamoe.mirai.api.http.adapter.internal.consts.Paths
import net.mamoe.mirai.api.http.adapter.uploading.uploadingRouter
import kotlin.test.Test
import kotlin.test.assertEquals

class UploadingRouterConflict {

    @Test
    fun testUploadingRouterConflict() = testWebsocketApplication {
        // no conflict
        installHttpAdapter()
        application {
            // no conflict
            uploadingRouter()
        }

        // response success
        client.submitFormWithBinaryData(Paths.httpPath(Paths.uploadImage), formData {
            append("path", "/")
        }).also {
            assertEquals(HttpStatusCode.OK, it.status)
        }
    }
}