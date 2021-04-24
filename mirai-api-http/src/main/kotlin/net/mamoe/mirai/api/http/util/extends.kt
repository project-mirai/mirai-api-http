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
