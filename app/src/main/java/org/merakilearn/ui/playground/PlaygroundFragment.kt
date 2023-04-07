package org.merakilearn.ui.playground

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.FileProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.auth.BasicSessionCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import kotlinx.android.synthetic.main.fragment_playground.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.merakilearn.R
import org.merakilearn.core.navigator.MerakiNavigator
import org.merakilearn.core.navigator.Mode
import org.merakilearn.datasource.UserRepo
import org.merakilearn.datasource.network.model.TempCredentialResponse
import org.merakilearn.ui.ScratchActivity
import org.merakilearn.util.Constants
import org.navgurukul.commonui.platform.BaseFragment
import org.navgurukul.commonui.platform.GridSpacingDecorator
import org.navgurukul.commonui.platform.ToolbarConfigurable
import java.io.File
import java.util.stream.DoubleStream.builder

class PlaygroundFragment : BaseFragment() {

    private val viewModel: PlaygroundViewModel by viewModel()
    private val navigator: MerakiNavigator by inject()
    var isLoading: Boolean = false
    lateinit var exportFile: File
    private val userRepo = UserRepo
//    val ACCESS_KEY = "AKIA3YAIMTT5NMBWXJUY"
//    val SECRET_KEY = "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"
//    val BUCKET_NAME = "chanakya-dev"

    override fun getLayoutResId() = R.layout.fragment_playground

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_view.layoutManager = GridLayoutManager(context, 4)
        initSearchListener()

        val spacings = resources.getDimensionPixelSize(R.dimen.spacing_3x)
        recycler_view.addItemDecoration(GridSpacingDecorator(spacings, spacings, 4))

        val adapter =
            PlaygroundAdapter(requireContext()) { playgroundItemModel, view, isLongClick ->
                if (isLongClick)
                    showUpPopMenu(playgroundItemModel.file, view)
                else
                    viewModel.selectPlayground(playgroundItemModel)

            }
        if (isLoading) showLoading() else dismissLoadingDialog()
        recycler_view.adapter = adapter

        viewModel.viewState.observe(viewLifecycleOwner) {
            adapter.setData(it.playgroundsList)
        }

        viewModel.viewEvents.observe(viewLifecycleOwner) {
            when (it) {
                is PlaygroundViewEvents.OpenPythonPlayground -> navigator.openPlayground(
                    requireContext()
                )
                is PlaygroundViewEvents.OpenTypingApp -> navigator.launchTypingApp(
                    requireActivity(),
                    Mode.Playground
                )
                is PlaygroundViewEvents.OpenPythonPlaygroundWithFile -> navigator.openPlaygroundWithFileContent(
                    requireActivity(),
                    file = it.file
                )
                is PlaygroundViewEvents.OpenScratch -> {
                    val intent = Intent(requireContext(), ScratchActivity::class.java)
                    startActivity(intent)
                }
                is PlaygroundViewEvents.OpenScratchWithFile -> {
                    val intent = Intent(requireContext(), ScratchActivity::class.java)
                    intent.putExtra(Constants.INTENT_EXTRA_KEY_FILE, it.file)
                    startActivity(intent)
                }
            }
        }

        (activity as? ToolbarConfigurable)?.configure(
            getString(R.string.title_playground),
            R.attr.textPrimary
        )
    }

    private fun showUpPopMenu(file: File, view: View) {
        val popup = PopupMenu(requireContext(), view)
        popup.menuInflater.inflate(R.menu.popup_menu_file_saved, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.delete -> viewModel.handle(PlaygroundActions.DeleteFile(file))
                R.id.shareSavedFile -> {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "text/x-python"
                    val uri = FileProvider.getUriForFile(requireContext(),"org.merakilearn.fileprovider",file)
                    intent.putExtra(Intent.EXTRA_STREAM, uri)
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Share File")
                    intent.putExtra(Intent.EXTRA_TEXT, "Sharing File")
                    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    startActivity(Intent.createChooser(intent, "Share File"))
                }
                R.id.exportSavedFile -> {
                    var mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension)
                    if(mimeType.isNullOrEmpty())
                        mimeType = "application/octet-stream"
                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = mimeType
                        putExtra(Intent.EXTRA_TITLE, file.name)
                        exportFile = file
                    }
                    startActivityForResult(intent, 100)
                }
                R.id.saveToServer -> {
                    Log.d("FILE UPLOADED","File uploaded savedTo server file ${file}")

//                    uploadFileToS3(file, BUCKET_NAME, ACCESS_KEY, SECRET_KEY)

                    viewModel.handle(PlaygroundActions.uploadScratchFile(file))
//                    Log.d("FILE UPLOADED","File uploaded successfully origin file ${file}")
//                    Log.d("FILE UPLOADED","File uploaded successfully origin bucket ${BUCKET_NAME}")
//                    Log.d("FILE UPLOADED","File uploaded successfully origin access ${ACCESS_KEY}")
//                    Log.d("FILE UPLOADED","File uploaded successfully origin secreat ${SECRET_KEY}")


                }
            }
            true
        }
        popup.show()
    }

    private fun initSearchListener() {
        search_view.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.handle(PlaygroundActions.Query(query))
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.handle(PlaygroundActions.Query(newText))
                return false
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100 && resultCode == Activity.RESULT_OK){
            val fileUri = data!!.data
            try {
                val outputStream = requireContext().contentResolver.openOutputStream(fileUri!!)
                outputStream?.write(exportFile.readBytes())
                outputStream?.close()
                Toast.makeText(requireContext(),"File exported successfully!", Toast.LENGTH_SHORT).show()
            }
            catch (e: Exception){
                Toast.makeText(requireContext(),"File exported incorrectly!", Toast.LENGTH_SHORT).show()
                println(e.localizedMessage)
            }
        } else if (requestCode == 100 && resultCode != Activity.RESULT_OK){
            Toast.makeText(requireContext(), "File export failed!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.handle(PlaygroundActions.RefreshLayout)
    }

    private fun uploadFileToS3(file: File, bucketName: String, accessKey: String, secretKey: String, sessionToken: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val region = com.amazonaws.regions.Region.getRegion(Regions.AP_SOUTH_1)
                val credentials = BasicSessionCredentials(accessKey, secretKey, sessionToken)
                val s3Client = AmazonS3Client(credentials)
                s3Client.setRegion(region)
                val metadata = ObjectMetadata()
                metadata.contentType = "application/vnd.android.package-archive"
                metadata.contentLength = file.length()
                val putObjectRequest = PutObjectRequest(bucketName, "scratch/${file.name}", file)
                Log.d("FILE UPLOADED","File uploaded successfully origin putObjecetRequest ${putObjectRequest}")
                Log.d("FILE UPLOADED","File uploaded successfully origin PutObjecetRequest ${PutObjectRequest(bucketName, file.name, file)}")
                putObjectRequest.metadata = metadata
                s3Client.putObject(putObjectRequest)
                val objectMetadata = s3Client.getObjectMetadata(bucketName, file.name)
                Log.d("FILE UPLOADED","File uploaded successfully. ETag: ${objectMetadata.eTag}")
            }catch (e: Exception) {
                Log.e("FILE UPLOADED", "Failed to upload file: ${e.message}")
            }
        }
    }


}