package org.navgurukul.playground.ui

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_playground.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.merakilearn.core.navigator.MerakiNavigator
import org.navgurukul.commonui.platform.BaseFragment
import org.navgurukul.commonui.platform.GridSpacingDecorator
import org.navgurukul.commonui.platform.ToolbarConfigurable
import org.navgurukul.playground.R

class PlaygroundFragment : BaseFragment() {

    private val viewModel: PlaygroundViewModel by viewModel()
    private val navigator: MerakiNavigator by inject()
    private val TYPE = "practicetyping";
    private val TYPING_CONTENT = arrayListOf("a","b","c","d","e","f","g",
            "h","i","j","k","l","m","n","o","p","q",
            "r","s","t","u","v","w","x","y","z")

    override fun getLayoutResId() = R.layout.fragment_playground

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_view.layoutManager = GridLayoutManager(context, 2)

        val spacings = resources.getDimensionPixelSize(R.dimen.spacing_3x)
        recycler_view.addItemDecoration(GridSpacingDecorator(spacings, spacings, 2))

        val adapter = PlaygroundAdapter(requireContext()) {
            viewModel.selectPlayground(it)
        }
        recycler_view.adapter = adapter

        viewModel.viewState.observe(viewLifecycleOwner, {
            adapter.setData(it.playgroundsList)
        })

        viewModel.viewEvents.observe(viewLifecycleOwner, {
            when (it) {
                PlaygroundViewEvents.HideLoading -> dismissLoadingDialog()
                PlaygroundViewEvents.OpenPythonPlayground -> navigator.openPlayground(requireContext(), "")
                PlaygroundViewEvents.OpenTypingApp -> navigator.launchTypingApp(requireActivity(), TYPING_CONTENT, TYPE)
                PlaygroundViewEvents.ShowLoading -> showLoading(getString(R.string.installing_module_message))
            }
        })

        (activity as? ToolbarConfigurable)?.configure(getString(R.string.title_playground), null, R.attr.textPrimary)
    }

}