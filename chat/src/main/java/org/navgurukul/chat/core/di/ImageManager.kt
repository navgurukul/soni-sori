package org.navgurukul.chat.core.di

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import org.matrix.android.sdk.api.session.Session
import org.navgurukul.chat.core.glide.FactoryUrl
import org.navgurukul.chat.core.repo.ActiveSessionDataSource
import java.io.InputStream

/**
 * This class is used to configure the library we use for images
 */
class ImageManager constructor(
    private val context: Context,
    private val activeSessionDataSource: ActiveSessionDataSource
) {

    fun onSessionStarted(session: Session) {
        // Do this call first
//        BigImageViewer.initialize(GlideImageLoader.with(context, session.getOkHttpClient()))

        val glide = Glide.get(context)

        // And this one. FIXME But are losing what BigImageViewer has done to add a Progress listener
        glide.registry.replace(GlideUrl::class.java, InputStream::class.java, FactoryUrl(activeSessionDataSource))
    }
}