package org.navgurukul.chat.features.reactions

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_generic_recycler.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.navgurukul.chat.R
import org.navgurukul.chat.core.extensions.cleanup
import org.navgurukul.chat.core.extensions.configureWith
import org.navgurukul.commonui.platform.BaseFragment

class EmojiSearchResultFragment: BaseFragment(), ReactionClickListener {

    private val epoxyController: EmojiSearchResultController by inject()

    override fun getLayoutResId() = R.layout.fragment_generic_recycler

    private val viewModel: EmojiSearchResultViewModel by sharedViewModel()

    private val sharedViewModel: EmojiChooserViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        epoxyController.listener = this
        recyclerView.configureWith(epoxyController, showDivider = true)
        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            epoxyController.setData(it)
        })
    }

    override fun onDestroyView() {
        epoxyController.listener = null
        recyclerView.cleanup()
        super.onDestroyView()
    }

    override fun onReactionSelected(reaction: String) {
        sharedViewModel.selectedReaction = reaction
        sharedViewModel.navigateEvent.setValue(EmojiChooserViewModel.NAVIGATE_FINISH)
    }

}
