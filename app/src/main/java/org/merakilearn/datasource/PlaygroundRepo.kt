package org.merakilearn.datasource


import org.merakilearn.R
import org.merakilearn.datasource.model.PlaygroundItemModel
import org.merakilearn.datasource.model.PlaygroundTypes
import org.merakilearn.datasource.network.SaralApi
import org.merakilearn.datasource.network.model.ProjectNameAndUrl
import org.merakilearn.datasource.network.model.UpdateSuccessS3UploadResponse
import org.merakilearn.datasource.network.model.UploadCredentials
import org.navgurukul.learn.courses.network.wrapper.BaseRepo
import org.navgurukul.learn.courses.network.wrapper.Resource
import timber.log.Timber

class PlaygroundRepo(
    private val api: SaralApi,
): BaseRepo() {

    fun getAllPlaygrounds(): List<PlaygroundItemModel> {
        return arrayListOf(
            PlaygroundItemModel(
                PlaygroundTypes.PYTHON,
                name = "Python",
                iconResource = R.drawable.python_logo,
            ),
            PlaygroundItemModel(
                PlaygroundTypes.TYPING_APP,
                name = "Typing",
                iconResource = R.drawable.ic_icon_typing,
            ),
            PlaygroundItemModel(
                PlaygroundTypes.SCRATCH,
                name = "Scratch",
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

    suspend fun updateSuccessS3Upload(projectId: String, projectNameAndUrl: ProjectNameAndUrl): Resource<UpdateSuccessS3UploadResponse>? {
        return try {
            safeApiCall { api.updateSuccessS3Upload(projectId, projectNameAndUrl) }
        } catch (ex: Exception) {
            Timber.tag("PLAYGROUND_REPO").e(ex,"updateSuccessS3UploadResponse")
            null
        }
    }
}