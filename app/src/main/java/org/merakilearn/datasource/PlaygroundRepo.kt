package org.merakilearn.datasource

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.merakilearn.R
import org.merakilearn.datasource.model.PlaygroundItemModel
import org.merakilearn.datasource.model.PlaygroundTypes
import org.merakilearn.datasource.network.SaralApi
import org.merakilearn.datasource.network.model.UploadCredentials
import timber.log.Timber

class PlaygroundRepo(
    private val api: SaralApi
) {

    fun getAllPlaygrounds(): List<PlaygroundItemModel> {
        return arrayListOf(
            PlaygroundItemModel(
                PlaygroundTypes.PYTHON,
                name="Python",
                iconResource = R.drawable.python_logo,
            ),
            PlaygroundItemModel(
                PlaygroundTypes.TYPING_APP,
                name="Typing",
                iconResource = R.drawable.ic_icon_typing,
            ),
            PlaygroundItemModel(
                PlaygroundTypes.SCRATCH,
                name="Scratch",
                iconResource = R.drawable.ic_scratch_cat,
            ),

        )
    }

    suspend fun getUploadCredentials(): UploadCredentials? {
        return try {
            val response = api.getUploadCredentials()
            response
        } catch (ex: Exception) {
            Timber.tag("PLAYGROUND_REPO").e(ex, "getUploadCredentials: ")
            null
        }
    }
}