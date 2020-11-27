package org.navgurukul.playground.repo

interface PlaygroundRepository {
    fun getCachedCode(): String
    fun cacheCode(code: String)
    fun saveCode(code: String, fileName: String)
}