/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

/*
 * Copy from ktor. io.ktor.http.url.kt
 */

package net.mamoe.mirai.api.http.util

import io.ktor.http.*

fun URLBuilder.smartTakeFrom(urlString: String): URLBuilder {
    return try {
        takeFromUnsafe(urlString)
    } catch (cause: Throwable) {
        throw URLParserException(urlString, cause)
    }
}

private fun URLBuilder.takeFromUnsafe(urlString: String): URLBuilder {
    var startIndex = urlString.indexOfFirst { !it.isWhitespace() }
    val endIndex = urlString.indexOfLast { !it.isWhitespace() } + 1

    val schemeLength = findScheme(urlString, startIndex, endIndex)
    if (schemeLength > 0) {
        val scheme = urlString.substring(startIndex, startIndex + schemeLength)

        protocol = URLProtocol.createOrDefault(scheme)
        startIndex += schemeLength + 1
    }

    // Auth & Host
    val slashCount = count(urlString, startIndex, endIndex, '/')
    startIndex += slashCount

    if (protocol.name == "file") {
        parseFile(urlString, startIndex, endIndex, slashCount)
        return this
    }

    if (protocol.name == "mailto") {
        require(slashCount == 0)
        parseMailto(urlString, startIndex, endIndex)
        return this
    }

    val delimiterArr = "@/\\?#".toCharArray()
    if (slashCount >= 2) {
        loop@ while (true) {
            val delimiter = urlString.indexOfAny(delimiterArr, startIndex).takeIf { it > 0 } ?: endIndex

            if (delimiter < endIndex && urlString[delimiter] == '@') {
                // user and password check
                val passwordIndex = urlString.indexOfColonInHostPort(startIndex, delimiter)
                if (passwordIndex != -1) {
                    user = urlString.substring(startIndex, passwordIndex).decodeURLPart()
                    password = urlString.substring(passwordIndex + 1, delimiter).decodeURLPart()
                } else {
                    user = urlString.substring(startIndex, delimiter).decodeURLPart()
                }
                startIndex = delimiter + 1
            } else {
                fillHost(urlString, startIndex, delimiter)
                startIndex = delimiter
                break@loop
            }
        }
    } else if (slashCount == 0) {
        val delimiter = urlString.indexOfAny(delimiterArr, startIndex).takeIf { it > 0 } ?: endIndex
        fillHost(urlString, startIndex, delimiter)
        startIndex = delimiter + 1
    }

    // Path
    if (startIndex >= endIndex) {
        encodedPath = if (urlString[endIndex - 1] == '/') "/" else ""
        return this
    }

    encodedPath = if (slashCount == 0) {
        // Relative path
        val lastSlashIndex = encodedPath.lastIndexOf('/')

        if (lastSlashIndex != encodedPath.length - 1) {
            // Current path does not end in slash, get rid of last path segment.
            if (lastSlashIndex != -1) {
                encodedPath.substring(0, lastSlashIndex + 1)
            } else {
                "/"
            }
        } else {
            // keep the whole path
            encodedPath
        }
    } else {
        // overwrite the path
        ""
    }

    val pathEnd = urlString.indexOfAny("?#".toCharArray(), startIndex).takeIf { it > 0 } ?: endIndex
    val rawPath = urlString.substring(startIndex, pathEnd)

    encodedPath += rawPath.encodeURLPath()
    startIndex = pathEnd

    // Query
    if (startIndex < endIndex && urlString[startIndex] == '?') {
        startIndex = parseQuery(urlString, startIndex, endIndex)
    }

    // Fragment
    parseFragment(urlString, startIndex, endIndex)
    return this
}

private fun URLBuilder.parseFile(urlString: String, startIndex: Int, endIndex: Int, slashCount: Int) {
    when (slashCount) {
        2 -> {
            val nextSlash = urlString.indexOf('/', startIndex)
            if (nextSlash == -1 || nextSlash == endIndex) {
                host = urlString.substring(startIndex, endIndex)
                return
            }

            host = urlString.substring(startIndex, nextSlash)
            encodedPath = urlString.substring(nextSlash, endIndex)
        }
        3 -> {
            host = ""
            encodedPath = "/" + urlString.substring(startIndex, endIndex)
        }
        else -> throw IllegalArgumentException("Invalid file url: $urlString")
    }
}

private fun URLBuilder.parseMailto(urlString: String, startIndex: Int, endIndex: Int) {
    val delimiter = urlString.indexOf("@", startIndex)
    if (delimiter == -1) {
        throw IllegalArgumentException("Invalid mailto url: $urlString, it should contain '@'.")
    }

    user = urlString.substring(startIndex, delimiter).decodeURLPart()
    host = urlString.substring(delimiter + 1, endIndex)
}

private fun URLBuilder.parseQuery(urlString: String, startIndex: Int, endIndex: Int): Int {
    if (startIndex + 1 == endIndex) {
        trailingQuery = true
        return endIndex
    }

    val fragmentStart = urlString.indexOf('#', startIndex + 1).takeIf { it > 0 } ?: endIndex

    val rawParameters = parseQueryString(urlString.substring(startIndex + 1, fragmentStart))
    rawParameters.forEach { key, values ->
        parameters.appendAll(key, values)
    }

    return fragmentStart
}

private fun URLBuilder.parseFragment(urlString: String, startIndex: Int, endIndex: Int) {
    if (startIndex < endIndex && urlString[startIndex] == '#') {
        fragment = urlString.substring(startIndex + 1, endIndex)
    }
}

private fun URLBuilder.fillHost(urlString: String, startIndex: Int, endIndex: Int) {
    val colonIndex = urlString.indexOfColonInHostPort(startIndex, endIndex).takeIf { it > 0 } ?: endIndex

    host = urlString.substring(startIndex, colonIndex)

    port = if (colonIndex + 1 < endIndex) {
        urlString.substring(colonIndex + 1, endIndex).toInt()
    } else {
        DEFAULT_PORT
    }
}

private fun findScheme(urlString: String, startIndex: Int, endIndex: Int): Int {
    var current = startIndex

    // Incorrect scheme position is used to identify the first position at which the character is not allowed in the
    // scheme or the part of the scheme. This number is reported in the exception message.
    var incorrectSchemePosition = -1
    val firstChar = urlString[current]
    if (firstChar !in 'a'..'z' && firstChar !in 'A'..'Z') {
        incorrectSchemePosition = current
    }

    while (current < endIndex) {
        val char = urlString[current]

        // Character ':' means the end of the scheme and at this point the length of the scheme should be returned or
        // the exception should be thrown in case the scheme contains illegal characters.
        if (char == ':' && current + 2 < endIndex
            && urlString[current+2] == '/'
            && urlString[current+1] == '/'
        ) {
            if (incorrectSchemePosition != -1) {
                throw IllegalArgumentException("Illegal character in scheme at position $incorrectSchemePosition")
            }

            return current - startIndex
        }

        // If character '/' or '?' or '#' found this is not a scheme.
        if (char == '/' || char == '?' || char == '#') return -1

        // Update incorrect scheme position is current char is illegal.
        if (incorrectSchemePosition == -1 &&
            char !in 'a'..'z' &&
            char !in 'A'..'Z' &&
            char !in '0'..'9' &&
            char != '.' &&
            char != '+' &&
            char != '-'
        ) {
            incorrectSchemePosition = current
        }

        ++current
    }

    return -1
}

private fun count(urlString: String, startIndex: Int, endIndex: Int, char: Char): Int {
    var result = 0
    while (startIndex + result < endIndex) {
        if (urlString[startIndex + result] != char) break
        result++
    }

    return result
}

private fun String.indexOfColonInHostPort(startIndex: Int, endIndex: Int): Int {
    var skip = false
    for (index in startIndex until endIndex) {
        when (this[index]) {
            '[' -> skip = true
            ']' -> skip = false
            ':' -> if (!skip) return index
        }
    }

    return -1
}
