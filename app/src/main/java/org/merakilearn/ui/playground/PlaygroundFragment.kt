package org.merakilearn.ui.playground

import android.app.Activity
import android.content.SharedPreferences
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.merakilearn.R
import org.merakilearn.util.webide.Prefs.set
import org.merakilearn.util.webide.Prefs.get
import org.merakilearn.core.navigator.MerakiNavigator
import org.merakilearn.core.navigator.Mode
import org.merakilearn.databinding.DialogCreateBinding
import org.merakilearn.databinding.FragmentPlaygroundBinding
import org.merakilearn.datasource.model.PlaygroundTypes
import org.merakilearn.ui.ScratchActivity
import org.merakilearn.util.Constants
import org.merakilearn.util.webide.Prefs
import org.merakilearn.util.webide.ROOT_PATH
import org.merakilearn.util.webide.project.DataValidator
import org.merakilearn.util.webide.project.ProjectManager
import org.navgurukul.commonui.platform.BaseFragment
import org.navgurukul.commonui.platform.GridSpacingDecorator
import org.navgurukul.commonui.platform.ToolbarConfigurable
import java.io.File
import java.io.InputStream
import java.util.*

import org.merakilearn.ui.ArduinoBlocklyActivity

class PlaygroundFragment : BaseFragment() {

    private val viewModel: PlaygroundViewModel by viewModel()
    private val navigator: MerakiNavigator by inject()
    var isLoading: Boolean = false
    lateinit var exportFile: File
    private lateinit var binding: FragmentPlaygroundBinding

    private var contents: Array<String>? = null
    private var contentsList: ArrayList<String>? = null
    private lateinit var adapter: PlaygroundAdapter

    private lateinit var prefs: SharedPreferences
    private var imageStream: InputStream? = null
    private lateinit var projectIcon: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaygroundBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun getLayoutResId() = R.layout.fragment_playground

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = GridLayoutManager(context, 4)
        initSearchListener()

        val spacings = resources.getDimensionPixelSize(org.navgurukul.commonui.R.dimen.spacing_3x)
        binding.recyclerView.addItemDecoration(GridSpacingDecorator(spacings, spacings, 4))

        adapter =
            PlaygroundAdapter(requireContext()) { playgroundItemModel, view, isLongClick ->

                val viewState = viewModel.viewState.value
                viewState?.let { state ->
                    if (playgroundItemModel.type == PlaygroundTypes.SCRATCH) {
                      //  ScratchActivity.start(requireContext())
                    }
                }
                if (isLongClick)
                    if (playgroundItemModel.type == PlaygroundTypes.WEB_IDE_FILES){
                        playgroundItemModel.webFile?.let { showAlertDialogWeb(it) }
                    } else{
                        showUpPopMenu(playgroundItemModel.file, view)
                    }
                else
                    viewModel.selectPlayground(playgroundItemModel)

            }
        if (isLoading) showLoading() else dismissLoadingDialog()
        binding.recyclerView.adapter = adapter

        viewModel.viewState.observe(viewLifecycleOwner) {
            adapter.setData(it.playgroundsList)
        }

        viewModel.viewEvents.observe(viewLifecycleOwner) {
            when (it) {
                is PlaygroundViewEvents.OpenPythonPlayground -> navigator.openPlayground(
                    requireContext(), null, isFromCourse = false
                )
                is PlaygroundViewEvents.OpenTypingApp -> navigator.launchTypingApp(
                    requireActivity(),
                    Mode.Playground
                )
                is PlaygroundViewEvents.OpenPythonPlaygroundWithFile -> navigator.openPlaygroundWithFileContent(
                    requireActivity(),
                    file = it.file
                )
                is PlaygroundViewEvents.OpenWebIDE -> {
                    setUpWebFiles(it.project)
                }
                is PlaygroundViewEvents.OpenDialogToCreateWebProject -> {
                    openDialogToCreateProject()
                }
                is PlaygroundViewEvents.OpenScratch -> {
                    val intent = Intent(requireContext(), ScratchActivity::class.java)
                    startActivity(intent)
                }
                is PlaygroundViewEvents.OpenScratchWithFile -> {
                    val intent = Intent(requireContext(), ScratchActivity::class.java)
                    intent.putExtra(Constants.INTENT_EXTRA_KEY_FILE, it.file)
                    startActivity(intent)
                }
                is PlaygroundViewEvents.OpenArduinoBlockly -> {
//                    val intent = Intent(requireContext(), ArduinoBlocklyActivity::class.java)
//                    startActivity(intent)
                }
            }
        }

        (activity as? ToolbarConfigurable)?.configure(
            getString(org.navgurukul.playground.R.string.title_playground),
            org.navgurukul.commonui.R.attr.textPrimary
        )

    }

    private fun setUpWebFiles(project : String){
            try {
                navigator.launchWebIDEApp(requireActivity(), project)
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
    }

    private fun openDialogToCreateProject() {
        prefs = Prefs.defaultPrefs(requireContext())
        val dialogBinding = DialogCreateBinding.inflate(LayoutInflater.from(requireContext()))
        dialogBinding.nameLayout.editText!!.setText("")

        projectIcon = dialogBinding.faviconImage

        val createDialog = AlertDialog.Builder(requireContext())
            .setTitle("Create a new project")
            .setView(dialogBinding.root)
            .setPositiveButton("CREATE", null)
            .setNegativeButton("CANCEL", null)
            .create()

        createDialog.show()

        // Set the color of the negative button
        val negativeButton = createDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        negativeButton.setTextColor(Color.RED)

        createDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            viewModel.handle(PlaygroundActions.RefreshLayout)
            if (DataValidator.validateCreate(requireContext(), dialogBinding.nameLayout)) {
                val name = dialogBinding.nameLayout.editText!!.text.toString()

                prefs["name"] = name
                prefs["type"] = 0

                Log.i("TAG", requireActivity().ROOT_PATH())
                val projectName = ProjectManager.generate(
                    requireContext(),
                    name,
                    imageStream,
                    adapter,
                    binding.coordinatorLayout,
                    0
                )
                adapter.notifyDataSetChanged()

                //var intent: Intent? = null
                try {
                    navigator.launchWebIDEApp(requireActivity(), projectName)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                createDialog.dismiss()
            }
        }
    }

    private fun showAlertDialogWeb(project: String){
            view?.let {
                AlertDialog.Builder(it.context)
                    .setTitle("${requireView().context.getString(R.string.delete)} $project?")
                    .setMessage(R.string.change_undone)
                    .setPositiveButton(R.string.delete) { _, _ ->
                        viewModel.handle(PlaygroundActions.DeleteWebFile(project))
                        Toast.makeText(requireContext(), "Deleted $project.", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .show()
            }
    }

    private fun showUpPopMenu(file: File, view: View) {
        val popup = PopupMenu(requireContext(), view)
        popup.menuInflater.inflate(R.menu.popup_menu_saved_file, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.delete -> {
                        AlertDialog.Builder(requireContext())
                            .setTitle("Delete file")
                            .setMessage("Are you sure you want to delete this file?")
                            .setPositiveButton("Delete") { _, _ ->
                                viewModel.handle(PlaygroundActions.DeleteFile(file))
                            }
                            .setNegativeButton("Cancel", null)
                            .show()
                }
                org.navgurukul.playground.R.id.shareSavedFile -> {
                    try {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/x-python"
                        val uri = FileProvider.getUriForFile(
                            requireContext(),
                            "org.merakilearn.fileprovider",
                            file
                        )
                        intent.putExtra(Intent.EXTRA_STREAM, uri)
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Share File")
                        intent.putExtra(Intent.EXTRA_TEXT, "Sharing File")
                        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        startActivity(intent)  //Passing the intent Instead  create chooser for SecurityException
                    }catch (e: Exception){
                        Toast.makeText(requireContext(), "File sharing failed!", Toast.LENGTH_SHORT).show()
                    }
                }
                org.navgurukul.playground.R.id.exportSavedFile -> {
                    var mimeType =
                        MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension)
                    if (mimeType.isNullOrEmpty())
                        mimeType = "application/octet-stream"
                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = mimeType
                        putExtra(Intent.EXTRA_TITLE, file.name)
                        exportFile = file
                    }
                    startActivityForResult(intent, 100)
                }
                org.navgurukul.playground.R.id.shareAsUrl -> viewModel.handle(
                    PlaygroundActions.ShareAsUrl(
                        file,
                        requireContext()
                    )
                )

            }
            true
        }
        popup.show()
    }

    private fun initSearchListener() {
        binding.searchView.setOnQueryTextListener(object :
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

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            val fileUri = data!!.data
            try {
                val outputStream = requireContext().contentResolver.openOutputStream(fileUri!!)
                outputStream?.write(exportFile.readBytes())
                outputStream?.close()
                Toast.makeText(requireContext(), "File exported successfully!", Toast.LENGTH_SHORT)
                    .show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "File exported incorrectly!", Toast.LENGTH_SHORT)
                    .show()
                println(e.localizedMessage)
            }
        } else if (requestCode == 100 && resultCode != Activity.RESULT_OK) {
            Toast.makeText(requireContext(), "File export failed!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.handle(PlaygroundActions.RefreshLayout)
    }

    companion object {
        private const val SELECT_ICON = 100
        private const val SETTINGS_CODE = 101
        private const val IMPORT_PROJECT = 102
    }

}
interface fragmentViewBinding{
    fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
}