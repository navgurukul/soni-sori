package org.navgurukul.commonui.platform

import android.app.Activity
import android.content.Context
import android.graphics.drawable.PictureDrawable
import android.os.AsyncTask
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.caverock.androidsvg.SVG
import java.io.InputStream
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL

class SvgLoader(private val context: Context) {
    private val requestOptions = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .skipMemoryCache(true)

    fun loadSvgFromUrl(url: String, targetImageView: ImageView) {
        val task = SvgLoadingTask(context, targetImageView, requestOptions)
        task.execute(url)
    }

    private class SvgLoadingTask(
        context: Context,
        private val targetImageView: ImageView,
        private val requestOptions: RequestOptions
    ) : AsyncTask<String, Void, SVG?>() {
        private val contextReference: WeakReference<Context> = WeakReference(context)

        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg urls: String): SVG? {
            try {
                val urlConnection = URL(urls[0]).openConnection() as HttpURLConnection
                val inputStream: InputStream = urlConnection.inputStream
                return SVG.getFromInputStream(inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        @Deprecated("Deprecated in Java")
        override fun onPostExecute(svg: SVG?) {
            val context = contextReference.get()
            if (context != null && !isCancelled && svg != null) {
                val picture = svg.renderToPicture()
                val drawable = PictureDrawable(picture)
                if (context is Activity && !context.isFinishing) {
                    Glide.with(context)
                        .load(drawable)
                        .apply(requestOptions)
                        .into(targetImageView)
                }
            }
        }
    }
}