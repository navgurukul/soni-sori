package org.merakilearn.ui.playground

import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_playground.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.merakilearn.R
import org.merakilearn.core.navigator.MerakiNavigator
import org.merakilearn.core.navigator.Mode
import org.merakilearn.datasource.model.PlaygroundItemModel
import org.merakilearn.datasource.model.PlaygroundTypes
import org.merakilearn.ui.ScratchActivity
import org.navgurukul.commonui.platform.BaseFragment
import org.navgurukul.commonui.platform.GridSpacingDecorator
import org.navgurukul.commonui.platform.ToolbarConfigurable
import java.io.File

class PlaygroundFragment : BaseFragment() {

    private val viewModel: PlaygroundViewModel by viewModel()
    private val navigator: MerakiNavigator by inject()
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

        viewModel.viewState.observe(viewLifecycleOwner, {
            adapter.setData(it.playgroundsList)
        })

        viewModel.viewEvents.observe(viewLifecycleOwner, {
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
            }
        })

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


}