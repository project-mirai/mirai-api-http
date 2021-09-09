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