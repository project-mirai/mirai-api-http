/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.util

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
inline fun <T> whenTrueOrNull(condition: Boolean, blk: () -> T): T? {
    contract {
        callsInPlace(blk, InvocationKind.AT_MOST_ONCE)
    }

    return if (condition) blk() else null
}

@OptIn(ExperimentalContracts::class)
inline fun <T> whenTrueOrThrow(condition: Boolean, blk: () -> T): T {
    contract {
        callsInPlace(blk, InvocationKind.AT_MOST_ONCE)
    }

    return if (condition) blk() else throw IllegalStateException("condition is not true")
}

@OptIn(ExperimentalContracts::class)
inline fun <T> whenFalseOrNull(condition: Boolean, blk: () -> T): T? {
    contract {
        callsInPlace(blk, InvocationKind.AT_MOST_ONCE)
    }

    return if (!condition) blk() else null
}

@OptIn(ExperimentalContracts::class)
inline fun <T> whenFalseOrThrow(condition: Boolean, blk: () -> T): T {
    contract {
        callsInPlace(blk, InvocationKind.AT_MOST_ONCE)
    }

    return if (!condition) blk() else throw IllegalStateException("condition is not false")
}
