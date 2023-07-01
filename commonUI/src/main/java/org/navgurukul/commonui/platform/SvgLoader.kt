package org.navgurukul.commonui.platform

import android.app.Activity
import android.content.Context
import android.graphics.drawable.PictureDrawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.caverock.androidsvg.SVG
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class SvgLoader(private val context: Context) {
    private val requestOptions = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .skipMemoryCache(true)

    fun loadSvgFromUrl(url: String, targetImageView: ImageView) {
        val thread = object : Thread() {
            override fun run() {
                try {
                    val urlConnection = URL(url).openConnection() as HttpURLConnection
                    val inputStream: InputStream = urlConnection.inputStream
                    val svg = SVG.getFromInputStream(inputStream)
                    val picture = svg.renderToPicture()
                    val drawable = PictureDrawable(picture)
                    (context as? Activity)?.runOnUiThread {
                        Glide.with(context)
                            .load(drawable)
                            .apply(requestOptions)
                            .into(targetImageView)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        thread.start()
    }
}