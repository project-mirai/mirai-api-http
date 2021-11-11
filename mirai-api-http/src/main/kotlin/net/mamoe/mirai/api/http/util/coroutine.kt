/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

suspend fun <A, B, R> merge(task1: suspend () -> A, task2: suspend () -> B, consume: suspend (A, B) -> R): R {
    return withContext(Dispatchers.IO) {
        val r1 = async { task1() }
        val r2 = async { task2() }
        consume(r1.await(), r2.await())
    }
}