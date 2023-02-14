package org.merakilearn.ui.playground

import android.app.Activity
import android.content.SharedPreferences
import android.content.Intent
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.dialog_create.view.*
import kotlinx.android.synthetic.main.fragment_playground.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.merakilearn.R
import org.merakilearn.util.webide.Prefs.set
import org.merakilearn.util.webide.Prefs.get
import org.merakilearn.core.navigator.MerakiNavigator
import org.merakilearn.core.navigator.Mode
import org.merakilearn.ui.ScratchActivity
import org.merakilearn.util.Constants
import org.merakilearn.util.webide.Prefs
import org.merakilearn.util.webide.Prefs.get
import org.merakilearn.util.webide.Prefs.set
import org.merakilearn.util.webide.ROOT_PATH
import org.merakilearn.util.webide.adapter.ProjectAdapter
import org.merakilearn.util.webide.project.DataValidator
import org.merakilearn.util.webide.project.ProjectManager
import org.navgurukul.commonui.platform.BaseFragment
import org.navgurukul.commonui.platform.GridSpacingDecorator
import org.navgurukul.commonui.platform.ToolbarConfigurable
import java.io.File
import java.io.InputStream
import java.util.*
import java.io.OutputStream

class PlaygroundFragment : BaseFragment() {
    private val viewModel: PlaygroundViewModel by viewModel()
    private val navigator: MerakiNavigator by inject()
    var isLoading: Boolean = false
    lateinit var exportFile: File

    private var contents: Array<String>? = null
    private var contentsList: ArrayList<String>? = null
    private lateinit var projectAdapter: ProjectAdapter

    private lateinit var prefs: SharedPreferences
    private var imageStream: InputStream? = null
    private lateinit var projectIcon: ImageView

    override fun getLayoutResId() = R.layout.fragment_playground

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_view.layoutManager = GridLayoutManager(context, 4)
        initSearchListener()

        val spacings = resources.getDimensionPixelSize(R.dimen.spacing_3x)
        recycler_view.addItemDecoration(GridSpacingDecorator(spacings, spacings, 4))

        val adapter =
            PlaygroundAdapter(requireContext()) { playgroundItemModel, view, isLongClick ->

                val viewState = viewModel.viewState.value
                viewState?.let { state ->
                    if (playgroundItemModel.type == PlaygroundTypes.SCRATCH) {
                        ScratchActivity.start(requireContext())
                    }
                }
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
                is PlaygroundViewEvents.OpenWebIDE -> {
                    //   navigator.launchWebIDEApp(requireActivity(), Mode.Playground)
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
            }
        }

        (activity as? ToolbarConfigurable)?.configure(
            getString(R.string.title_playground),
            R.attr.textPrimary
        )

        setUpRecyclerViewForWebFiles()
    }

    private fun setUpRecyclerViewForWebFiles() {
        prefs = Prefs.defaultPrefs(requireContext())
        contents = File(requireContext().ROOT_PATH()).list { dir, name ->
            dir.isDirectory && name != ".git" && ProjectManager.isValid(
                requireContext(),
                name
            )
        }
        contentsList = if (contents != null) {
            ArrayList(Arrays.asList(*contents!!))
        } else {
            ArrayList()
        }

        Log.i("TAG", contentsList!!.size.toString())

        DataValidator.removeBroken(requireContext(), contentsList!!)

        projectAdapter = ProjectAdapter(
            requireActivity(),
            navigator,
            contentsList!!,
            coordinatorLayout,
            projectList
        )

        val layoutManager = GridLayoutManager(requireContext(), 4)
        projectList.layoutManager = layoutManager
        val spacings = resources.getDimensionPixelSize(R.dimen.spacing_3x)
        projectList.addItemDecoration(GridSpacingDecorator(spacings, spacings, 4))
        projectList.adapter = projectAdapter

    }

    private fun openDialogToCreateProject() {
        val rootView = View.inflate(requireContext(), R.layout.dialog_create, null)
//        rootView.typeSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, ProjectManager.TYPES)
//        rootView.typeSpinner.setSelection(prefs["type", 0]!!)
        rootView.nameLayout.editText!!.setText(prefs["name", ""])
//        rootView.authorLayout.editText!!.setText(prefs["author", ""])
//        rootView.descLayout.editText!!.setText(prefs["description", ""])
//        rootView.keyLayout.editText!!.setText(prefs["keywords", ""])

        projectIcon = rootView.faviconImage
//        rootView.defaultIcon.isChecked = true
//        rootView.defaultIcon.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                projectIcon.setImageResource(R.drawable.ic_launcher)
//                imageStream = null
//            }
//        }

//        rootView.chooseIcon.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                val intent = Intent(Intent.ACTION_GET_CONTENT)
//                intent.type = "image/*"
//                startActivityForResult(intent, SELECT_ICON)
//            }
//        }

        val createDialog = AlertDialog.Builder(requireContext())
            .setTitle("Create a new project")
            .setView(rootView)
            .setPositiveButton("CREATE", null)
            .setNegativeButton("CANCEL", null)
            .create()

        createDialog.show()
        createDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (DataValidator.validateCreate(requireContext(), rootView.nameLayout)) {
                val name = rootView.nameLayout.editText!!.text.toString()
//                val author = rootView.authorLayout.editText!!.text.toString()
//                val description = rootView.descLayout.editText!!.text.toString()
//                val keywords = rootView.keyLayout.editText!!.text.toString()
//                val type = rootView.typeSpinner.selectedItemPosition

                prefs["name"] = name
//                prefs["author"] = author
//                prefs["description"] = description
//                prefs["keywords"] = keywords
                prefs["type"] = 0

                Log.i("TAG", requireActivity().ROOT_PATH())
                val projectName = ProjectManager.generate(
                    requireContext(),
                    name,
                    imageStream,
                    projectAdapter,
                    coordinatorLayout,
                    0
                )
                projectAdapter.notifyDataSetChanged()

                //var intent: Intent? = null
                try {
                    navigator.launchWebIDEApp(requireActivity(), projectName)
//                    intent = Intent(context, Class.forName("org.navgurukul.webide.ui.activity.ProjectActivity"))
//                    intent.putExtra("project" ,projectName)
//                    context?.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                createDialog.dismiss()
            }
        }
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

    companion object {

        private const val SELECT_ICON = 100
        private const val SETTINGS_CODE = 101
        private const val IMPORT_PROJECT = 102
    }
}