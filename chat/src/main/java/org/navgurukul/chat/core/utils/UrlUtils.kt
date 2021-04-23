package org.navgurukul.chat.core.utils

import java.net.URL

fun String.isValidUrl(): Boolean {
    return try {
        URL(this)
        true
    } catch (t: Throwable) {
        false
    }
}

/**
 * Ensure string starts with "http". If it is not the case, "https://" is added, only if the String is not empty
 */
internal fun String.ensureProtocol(): String {
    return when {
        isEmpty()           -> this
        !startsWith("http") -> "https://$this"
        else                -> this
    }
}

internal fun String.ensureTrailingSlash(): String {
    return when {
        isEmpty()      -> this
        !endsWith("/") -> "$this/"
        else           -> this
    }
}