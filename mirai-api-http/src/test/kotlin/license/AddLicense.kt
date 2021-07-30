/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package license

import net.mamoe.mirai.api.http.util.visit
import java.io.File

object AddLicense {

    private val declare = """
        |/*
        | * Copyright 2020 Mamoe Technologies and contributors.
        | *
        | * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
        | * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
        | *
        | * https://github.com/mamoe/mirai/blob/master/LICENSE
        | */
        |
        |
    """.trimMargin().replace("\n", "\r\n")

    @JvmStatic
    fun main(args: Array<String>) {
        val baseFile = File("mirai-api-http/src")

        baseFile.visit {
            if (it.isFile && it.extension == "kt") {
                val content = it.readText()
                if (!content.startsWith(declare)) {
                    println(it.absolutePath)
                    it.writeText(declare + content)
                }
            }
            true
        }
    }
}