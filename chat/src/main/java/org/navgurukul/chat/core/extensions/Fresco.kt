package org.navgurukul.chat.core.extensions

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import com.facebook.common.references.CloseableReference
import com.facebook.datasource.BaseDataSubscriber
import com.facebook.datasource.DataSource
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.core.DefaultExecutorSupplier
import com.facebook.imagepipeline.image.CloseableBitmap
import com.facebook.imagepipeline.image.CloseableImage
import com.facebook.imagepipeline.request.ImageRequest

fun ImageRequest.getBitmap(context: Context, callback: (BitmapDrawable?) -> Unit) {
    val dataSource = Fresco.getImagePipeline().fetchDecodedImage(this, context)
    dataSource.subscribe(
        object : BaseDataSubscriber<CloseableReference<CloseableImage>>() {
            override fun onFailureImpl(dataSource: DataSource<CloseableReference<CloseableImage>>) {
                callback(null)
            }

            override fun onNewResultImpl(dataSource: DataSource<CloseableReference<CloseableImage>>) {
                if (!dataSource.isFinished) {
                    return
                }

                dataSource.result
                    ?.takeIf { it.get() is CloseableBitmap }
                    ?.let {
                        @Suppress("UNCHECKED_CAST")
                        callback(
                            BitmapDrawable(
                                context.resources,
                                (it.get() as CloseableBitmap).underlyingBitmap
                            )
                        )
                    }

            }
        },
        DefaultExecutorSupplier(1).forBackgroundTasks()
    )
}

fun ImageRequest.getBitmapFromCache(context: Context): BitmapDrawable? {
    val dataSource: DataSource<CloseableReference<CloseableImage>> =
        Fresco.getImagePipeline().fetchImageFromBitmapCache(this, context)
    return try {
        val imageReference = dataSource.result;
        if (imageReference != null) {
            try {
                BitmapDrawable(context.resources, (imageReference.get() as? CloseableBitmap)?.underlyingBitmap)
            } finally {
                CloseableReference.closeSafely(imageReference);
            }
        } else {
            null
        }
    } finally {
        dataSource.close();
    }
}