package org.navgurukul.chat.core.glide

import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.signature.ObjectKey
import org.matrix.android.sdk.api.Matrix
import okhttp3.OkHttpClient
import okhttp3.Request
import org.matrix.android.sdk.api.MatrixCallback
import org.matrix.android.sdk.api.session.file.FileService
import org.navgurukul.chat.core.repo.ActiveSessionHolder
import org.navgurukul.chat.features.media.ImageContentRenderer
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream

class SaralGlideModelLoaderFactory(private val activeSessionHolder: ActiveSessionHolder)
    : ModelLoaderFactory<ImageContentRenderer.Data, InputStream> {

    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<ImageContentRenderer.Data, InputStream> {
        return SaralGlideModelLoader(activeSessionHolder)
    }

    override fun teardown() {
        // Is there something to do here?
    }
}

class SaralGlideModelLoader(private val activeSessionHolder: ActiveSessionHolder)
    : ModelLoader<ImageContentRenderer.Data, InputStream> {
    override fun handles(model: ImageContentRenderer.Data): Boolean {
        // Always handle
        return true
    }

    override fun buildLoadData(model: ImageContentRenderer.Data, width: Int, height: Int, options: Options): ModelLoader.LoadData<InputStream>? {
        return ModelLoader.LoadData(ObjectKey(model), SaralGlideDataFetcher(activeSessionHolder, model, width, height))
    }
}

class SaralGlideDataFetcher(private val activeSessionHolder: ActiveSessionHolder,
                            private val data: ImageContentRenderer.Data,
                            private val width: Int,
                            private val height: Int)
    : DataFetcher<InputStream> {

    private val client = activeSessionHolder.getSafeActiveSession()?.getOkHttpClient() ?: OkHttpClient()

    override fun getDataClass(): Class<InputStream> {
        return InputStream::class.java
    }

    private var stream: InputStream? = null

    override fun cleanup() {
        cancel()
    }

    override fun getDataSource(): DataSource {
        // ?
        return DataSource.REMOTE
    }

    override fun cancel() {
        if (stream != null) {
            try {
                stream?.close() // interrupts decode if any
                stream = null
            } catch (ignore: IOException) {
                Timber.e(ignore)
            }
        }
    }

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        Timber.v("Load data: $data")
        if (data.isLocalFile && data.url != null) {
            val initialFile = File(data.url)
            callback.onDataReady(initialFile.inputStream())
            return
        }
//        val contentUrlResolver = activeSessionHolder.getActiveSession().contentUrlResolver()

        val fileService = activeSessionHolder.getSafeActiveSession()?.fileService() ?: return Unit.also {
            callback.onLoadFailed(IllegalArgumentException("No File service"))
        }
        // Use the file vector service, will avoid flickering and redownload after upload
        fileService.downloadFile(
            downloadMode = FileService.DownloadMode.FOR_INTERNAL_USE,
            mimeType = data.mimeType,
            id = data.eventId,
            url = data.url,
            fileName = data.filename,
            elementToDecrypt = data.elementToDecrypt,
            callback = object : MatrixCallback<File> {
                override fun onSuccess(data: File) {
                    callback.onDataReady(data.inputStream())
                }

                override fun onFailure(failure: Throwable) {
                    callback.onLoadFailed(failure as? Exception ?: IOException(failure.localizedMessage))
                }
            }
        )
//        val url = contentUrlResolver.resolveFullSize(data.url)
//                ?: return
//
//        val request = Request.Builder()
//                .url(url)
//                .build()
//
//        val response = client.newCall(request).execute()
//        val inputStream = response.body?.byteStream()
//        Timber.v("Response size ${response.body?.contentLength()} - Stream available: ${inputStream?.available()}")
//        if (!response.isSuccessful) {
//            callback.onLoadFailed(IOException("Unexpected code $response"))
//            return
//        }
//        stream = if (data.elementToDecrypt != null && data.elementToDecrypt.k.isNotBlank()) {
//            Matrix.decryptStream(inputStream, data.elementToDecrypt)
//        } else {
//            inputStream
//        }
//        callback.onDataReady(stream)
    }
}