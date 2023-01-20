package org.merakilearn.ui.playground

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.dialog_create.view.*
import kotlinx.android.synthetic.main.fragment_playground.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.merakilearn.R
import org.merakilearn.util.webide.Prefs.set
import org.merakilearn.util.webide.Prefs.get
import org.merakilearn.core.navigator.MerakiNavigator
import org.merakilearn.core.navigator.Mode
import org.merakilearn.datasource.model.PlaygroundTypes
import org.merakilearn.ui.ScratchActivity
import org.merakilearn.util.webide.Constants
import org.merakilearn.util.webide.Prefs
import org.merakilearn.util.webide.adapter.ProjectAdapter
import org.merakilearn.util.webide.project.DataValidator
import org.merakilearn.util.webide.project.ProjectManager
import org.navgurukul.commonui.platform.BaseFragment
import org.navgurukul.commonui.platform.GridSpacingDecorator
import org.navgurukul.commonui.platform.ToolbarConfigurable
import java.io.File
import java.io.InputStream
import java.util.*

class PlaygroundFragment : BaseFragment() {

    private val viewModel: PlaygroundViewModel by viewModel()
    private val navigator: MerakiNavigator by inject()
    var isLoading: Boolean = false

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
                    navigator.launchWebIDEApp(requireActivity(), Mode.Playground)
                }
                is PlaygroundViewEvents.OpenDialogToCreateWebProject ->{
                    openDialogToCreateProject()
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
        contents = File(Constants.HYPER_ROOT).list { dir, name -> dir.isDirectory && name != ".git" && ProjectManager.isValid(name) }
        contentsList = if (contents != null) {
            ArrayList(Arrays.asList(*contents!!))
        } else {
            ArrayList()
        }

        DataValidator.removeBroken(contentsList!!)

        projectAdapter = ProjectAdapter(requireContext(), contentsList!!, coordinatorLayout, projectList)

        val layoutManager = GridLayoutManager(requireContext(),4)
        projectList.layoutManager = layoutManager
        val spacings = resources.getDimensionPixelSize(R.dimen.spacing_3x)
        projectList.addItemDecoration(GridSpacingDecorator(spacings, spacings, 4))
        projectList.adapter = projectAdapter

    }

    private fun openDialogToCreateProject() {
        val rootView = View.inflate(requireContext(), R.layout.dialog_create, null)
        rootView.typeSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, ProjectManager.TYPES)
        rootView.typeSpinner.setSelection(prefs["type", 0]!!)
        rootView.nameLayout.editText!!.setText(prefs["name", ""])
        rootView.authorLayout.editText!!.setText(prefs["author", ""])
        rootView.descLayout.editText!!.setText(prefs["description", ""])
        rootView.keyLayout.editText!!.setText(prefs["keywords", ""])

        projectIcon = rootView.faviconImage
        rootView.defaultIcon.isChecked = true
        rootView.defaultIcon.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                projectIcon.setImageResource(R.drawable.ic_launcher)
                imageStream = null
            }
        }

        rootView.chooseIcon.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                startActivityForResult(intent, SELECT_ICON)
            }
        }

        val createDialog = AlertDialog.Builder(requireContext())
            .setTitle("Create a new project")
            .setView(rootView)
            .setPositiveButton("CREATE", null)
            .setNegativeButton("CANCEL", null)
            .create()

        createDialog.show()
        createDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (DataValidator.validateCreate(requireContext(), rootView.nameLayout, rootView.authorLayout, rootView.descLayout, rootView.keyLayout)) {
                val name = rootView.nameLayout.editText!!.text.toString()
                val author = rootView.authorLayout.editText!!.text.toString()
                val description = rootView.descLayout.editText!!.text.toString()
                val keywords = rootView.keyLayout.editText!!.text.toString()
                val type = rootView.typeSpinner.selectedItemPosition

                prefs["name"] = name
                prefs["author"] = author
                prefs["description"] = description
                prefs["keywords"] = keywords
                prefs["type"] = type

                ProjectManager.generate(
                    requireContext(),
                    name,
                    author,
                    description,
                    keywords,
                    imageStream,
                    projectAdapter,
                    coordinatorLayout,
                    type
                )

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