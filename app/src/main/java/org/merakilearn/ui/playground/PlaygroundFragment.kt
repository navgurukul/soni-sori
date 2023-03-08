package org.merakilearn.ui.playground

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_playground.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.merakilearn.R
import org.merakilearn.core.navigator.MerakiNavigator
import org.merakilearn.core.navigator.Mode
import org.merakilearn.datasource.model.PlaygroundItemModel
import org.merakilearn.datasource.model.PlaygroundTypes
import org.merakilearn.datasource.model.PlaygroundTypes.*
import org.merakilearn.repo.ScratchViewModel
import org.merakilearn.ui.ScratchActivity
import org.merakilearn.util.Constants
import org.navgurukul.commonui.platform.BaseFragment
import org.navgurukul.commonui.platform.GridSpacingDecorator
import org.navgurukul.commonui.platform.ToolbarConfigurable
import java.io.File

class PlaygroundFragment : BaseFragment() {

    private val viewModel: PlaygroundViewModel by viewModel()
    private val navigator: MerakiNavigator by inject()
    private val scratchViewModel : ScratchViewModel by viewModel()
    var isLoading: Boolean = false

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
                    showUpPopMenu(playgroundItemModel.file, view, playgroundItemModel.type)
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
                    Toast.makeText(requireContext(), it.s3link, Toast.LENGTH_SHORT).show()
                    //accessing s3link from the list for particular project

                    val intent = Intent(requireContext(), ScratchActivity::class.java)
                    intent.putExtra(Constants.INTENT_EXTRA_KEY_FILE, it.s3link)
                    startActivity(intent)
                }
            }
        }


        (activity as? ToolbarConfigurable)?.configure(
            getString(R.string.title_playground),
            R.attr.textPrimary
        )
    }

    private fun showUpPopMenu(file: File, view: View, playgroundTypes: PlaygroundTypes) {
        val popup = PopupMenu(requireContext(), view)
        popup.menuInflater.inflate(R.menu.popup_menu_file_saved, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.delete -> if(playgroundTypes == SCRATCH_FILE){
                    viewModel.handle(PlaygroundActions.DeleteScratchFile) }else{ viewModel.handle(PlaygroundActions.DeleteFile(file))}
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

//
//    fun openScratchWithFile(file: File, projectId : String){
//        viewModel.getScratchProject(projectId)
//        val intent = Intent(requireContext(), ScratchActivity::class.java)
//        intent.putExtra(Constants.INTENT_EXTRA_KEY_FILE, file)
//        startActivity(intent)
//    }

}