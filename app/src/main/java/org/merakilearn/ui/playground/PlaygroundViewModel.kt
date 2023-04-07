package org.merakilearn.ui.playground

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.amazonaws.auth.BasicSessionCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.merakilearn.R
import org.merakilearn.datasource.PlaygroundRepo
import org.merakilearn.datasource.UserRepo
import org.merakilearn.datasource.model.PlaygroundItemModel
import org.merakilearn.datasource.model.PlaygroundTypes
import org.merakilearn.datasource.network.model.TempCredentialResponse
import org.merakilearn.repo.ScratchRepository
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.ViewEvents
import org.navgurukul.commonui.platform.ViewModelAction
import org.navgurukul.commonui.platform.ViewState
import org.navgurukul.playground.repo.PythonRepository
import java.io.File

class PlaygroundViewModel(
    private val repository: PlaygroundRepo,
    private val pythonRepository: PythonRepository,
    private val scratchRepository: ScratchRepository,
    private val userRepo: UserRepo,
) :
    BaseViewModel<PlaygroundViewEvents, PlaygroundViewState>(PlaygroundViewState()) {

    private var currentQuery: String? = null
    private lateinit var playgroundsList: MutableList<PlaygroundItemModel>

    fun handle(action: PlaygroundActions) {
        when (action) {
            is PlaygroundActions.Query -> {
                currentQuery = action.query
                filterList()
            }
            is PlaygroundActions.RefreshLayout -> init()
            is PlaygroundActions.DeleteFile -> deleteFile(action.file)
//            is PlaygroundActions.uploadScratchFile -> getTempCredential(action.file, action.bucketName, action.accessKey, action.secretKey, action.sessionToke)
            is PlaygroundActions.uploadScratchFile -> getTempCredential(action.file)
        }
    }

    fun init() {
        viewModelScope.launch {
            setList()
        }
    }

    private fun filterList() {
        val list = playgroundsList ?: return
        viewModelScope.launch(Dispatchers.Default) {
            val filterList = list.filter {
                val filterQuery = currentQuery?.let { currentQuery ->
                    if (currentQuery.isNotEmpty()) {
                        val wordsToCompare = (it.name).split(" ") + it.file.name.replaceAfterLast("_", "").removeSuffix("_").split(" ")
                        wordsToCompare.find { word ->
                            word.startsWith(
                                currentQuery,
                                true
                            )
                        } != null
                    } else {
                        true
                    }
                } ?: true

                return@filter filterQuery
            }
            updateState(filterList)
        }
    }

    private fun updateState(list: List<PlaygroundItemModel>) {
        setState {
            copy(playgroundsList = list)
        }
    }

    private suspend fun setList() {
        playgroundsList = repository.getAllPlaygrounds().toMutableList()
        val savedFiles = pythonRepository.fetchSavedFiles()
        for (file in savedFiles) {
            playgroundsList.add(PlaygroundItemModel(PlaygroundTypes.PYTHON_FILE,
                name = "",
                file = file,
                iconResource = R.drawable.ic_saved_file))
        }
        val savedFiles2 = scratchRepository.fetchSavedFiles()
        for (file in savedFiles2) {
            playgroundsList.add(PlaygroundItemModel(PlaygroundTypes.SCRATCH_FILE,
                name = file.name.removeSuffix(".sb3"),
                file = file,
                iconResource = R.drawable.ic_scratch))
        }

        updateState(playgroundsList)
    }

    fun selectPlayground(playgroundItemModel: PlaygroundItemModel) {
        when (playgroundItemModel.type) {
            PlaygroundTypes.TYPING_APP -> _viewEvents.setValue(PlaygroundViewEvents.OpenTypingApp)
            PlaygroundTypes.PYTHON -> _viewEvents.postValue(PlaygroundViewEvents.OpenPythonPlayground)
            PlaygroundTypes.PYTHON_FILE -> _viewEvents.setValue(PlaygroundViewEvents.OpenPythonPlaygroundWithFile(
                playgroundItemModel.file))
            PlaygroundTypes.SCRATCH -> _viewEvents.postValue(PlaygroundViewEvents.OpenScratch)
            PlaygroundTypes.SCRATCH_FILE -> _viewEvents.postValue(PlaygroundViewEvents.OpenScratchWithFile(
                playgroundItemModel.file))
        }
    }

    private fun deleteFile(file: File) {
        viewModelScope.launch {
            scratchRepository.deleteFile(file)
            pythonRepository.deleteFile(file)
            init()
        }
    }

    private fun getTempCredential(file: File){
        CoroutineScope(Dispatchers.IO).launch {
            val tempCredentials = userRepo.getTempCredential().data
            val keys = tempCredentials?.Credentials
            try {
                val region = com.amazonaws.regions.Region.getRegion(Regions.AP_SOUTH_1)
                val credentials = BasicSessionCredentials(keys?.SecretAccessKey, keys?.SecretAccessKey, keys?.SessionToken)
                val s3Client = AmazonS3Client(credentials)
                s3Client.setRegion(region)
                val metadata = ObjectMetadata()
                metadata.contentType = "application/vnd.android.package-archive"
                metadata.contentLength = file.length()
                val putObjectRequest = PutObjectRequest(tempCredentials?.Bucket , "scratch/${file.name}", file)
                Log.d("FILE UPLOADED","File uploaded successfully origin putObjecetRequest ${putObjectRequest}")
                Log.d("FILE UPLOADED","File uploaded successfully origin PutObjecetRequest ${PutObjectRequest(tempCredentials?.Bucket, file.name, file)}")
                putObjectRequest.metadata = metadata
                s3Client.putObject(putObjectRequest)
                val objectMetadata = s3Client.getObjectMetadata(tempCredentials?.Bucket, file.name)
                Log.d("FILE UPLOADED","File uploaded successfully. ETag: ${objectMetadata.eTag}")
            }catch (e: Exception) {
                Log.e("FILE UPLOADED", "Failed to upload file: ${e.message}")
            }
        }
    }
}

sealed class PlaygroundViewEvents : ViewEvents {
    object OpenTypingApp : PlaygroundViewEvents()
    object OpenPythonPlayground : PlaygroundViewEvents()
    class OpenPythonPlaygroundWithFile(val file: File) : PlaygroundViewEvents()
    object OpenScratch : PlaygroundViewEvents()
    class OpenScratchWithFile(val file: File) : PlaygroundViewEvents()

}

sealed class PlaygroundActions : ViewModelAction {
    data class Query(val query: String?) : PlaygroundActions()
    object RefreshLayout : PlaygroundActions()
    class DeleteFile(val file: File) : PlaygroundActions()

    //    class uploadScratchFile(val tempCredential : TempCredentialResponse): PlaygroundActions()
   class uploadScratchFile(val file: File) : PlaygroundActions()
}

data class PlaygroundViewState(
    val playgroundsList: List<PlaygroundItemModel> = arrayListOf()
) : ViewState