package org.navgurukul.commonui.resources

import android.app.Application
import android.content.ComponentCallbacks
import android.content.Context
import android.content.res.Configuration
import android.util.LruCache
import androidx.annotation.*

class ResourceResolver private constructor(private val enableCache: Boolean) {
    private val resourceCache: LruCache<String, Int> by lazy { LruCache<String, Int>(CACHE_SIZE) }

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

    companion object {
        private const val CACHE_SIZE =
            1000 //our cache is just String->Int mapping 1000 entry shouldn't take much memory

        private lateinit var instance: ResourceResolver

        @JvmStatic
        fun init(app: Application, enableCache: Boolean) {
            instance = ResourceResolver(enableCache)
            app.registerComponentCallbacks(object : ComponentCallbacks {
                override fun onLowMemory() {
                    instance.clearCache()
                }

                override fun onConfigurationChanged(newConfig: Configuration?) {
                    instance.clearCache()
                }
            })
        }

        @JvmStatic
        @StyleRes
        fun getStyleId(context: Context, stringName: String?) =
            instance.getResourceIdByName(context, "style", stringName)

        @JvmStatic
        @DrawableRes
        fun getDrawableId(context: Context, drawableName: String?) =
            instance.getResourceIdByName(context, "drawable", drawableName)

        @JvmStatic
        @ColorRes
        fun getColorId(context: Context, colorName: String?) =
            instance.getResourceIdByName(context, "color", colorName)

        @JvmStatic
        @StringRes
        fun getStringId(context: Context, stringName: String?) =
            instance.getResourceIdByName(context, "string", stringName)

        @JvmStatic
        @DimenRes
        fun getDimenId(context: Context, dimenName: String?) =
            instance.getResourceIdByName(context, "dimen", dimenName)

        @JvmStatic
        @AnyRes
        fun getResourceId(context: Context, type: String, resourceName: String?) =
            instance.getResourceIdByName(context, type, resourceName)
    }
}