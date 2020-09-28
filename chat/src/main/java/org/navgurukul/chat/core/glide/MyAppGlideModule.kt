package org.navgurukul.chat.core.glide

import android.content.Context
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import org.koin.java.KoinJavaComponent.inject
import org.navgurukul.chat.core.repo.ActiveSessionHolder
import org.navgurukul.chat.features.media.ImageContentRenderer
import java.io.InputStream

@GlideModule
class MyAppGlideModule : AppGlideModule() {

    private val activeSessionHolder by inject(ActiveSessionHolder::class.java)

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setLogLevel(Log.ERROR)
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.append(
            ImageContentRenderer.Data::class.java,
                InputStream::class.java,
                SaralGlideModelLoaderFactory(activeSessionHolder))
    }
}