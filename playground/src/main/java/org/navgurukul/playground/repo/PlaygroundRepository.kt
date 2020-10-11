package org.navgurukul.playground.repo

interface PlaygroundRepository {
    fun getCachedCode(): String
    fun cacheCode(code: String)
}