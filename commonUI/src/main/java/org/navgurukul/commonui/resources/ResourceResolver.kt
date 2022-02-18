package org.navgurukul.commonui.resources

import android.app.Application
import android.content.ComponentCallbacks
import android.content.Context
import android.content.res.Configuration
import android.util.LruCache
import androidx.annotation.*

class ResourceResolver constructor(private val enableCache: Boolean) {
    private val resourceCache: LruCache<String, Int> by lazy { LruCache<String, Int>(CACHE_SIZE) }

    private val CACHE_SIZE =
        1000 //our cache is just String->Int mapping 1000 entry shouldn't take much memory

    private var instance = this

    @AnyRes
    fun getResourceIdByName(context: Context, type: String, resourceName: String?): Int {
        if (!enableCache) {
            context.resources.getIdentifier(resourceName, type, context.packageName)
        }

        if (resourceName == null) {
            return 0
        }

        val resourceKey = "$type/$resourceName"
        return resourceCache.get(resourceKey)
            ?: context.resources.getIdentifier(resourceName, type, context.packageName)
                .also { resourceId -> resourceCache.put(resourceKey, resourceId) }
    }

    private fun clearCache() {
        resourceCache.evictAll()
    }

    @StyleRes
    fun getStyleId(context: Context, stringName: String?) =
        instance.getResourceIdByName(context, "style", stringName)

    @DrawableRes
    fun getDrawableId(context: Context, drawableName: String?) =
        instance.getResourceIdByName(context, "drawable", drawableName)

    @ColorRes
    fun getColorId(context: Context, colorName: String?) =
        instance.getResourceIdByName(context, "color", colorName)

    @StringRes
    fun getStringId(context: Context, stringName: String?) =
        instance.getResourceIdByName(context, "string", stringName)

    @DimenRes
    fun getDimenId(context: Context, dimenName: String?) =
        instance.getResourceIdByName(context, "dimen", dimenName)

    @AnyRes
    fun getResourceId(context: Context, type: String, resourceName: String?) =
        instance.getResourceIdByName(context, type, resourceName)
}