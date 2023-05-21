package org.navgurukul.chat.features.reactions

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.navgurukul.chat.R
import org.navgurukul.chat.core.extensions.cleanup
import org.navgurukul.chat.core.extensions.configureWith
import org.navgurukul.chat.databinding.FragmentGenericRecyclerBinding
import org.navgurukul.commonui.platform.BaseFragment

class EmojiSearchResultFragment: BaseFragment(), ReactionClickListener {

    private val epoxyController: EmojiSearchResultController by inject()

    override fun getLayoutResId() = R.layout.fragment_generic_recycler

    private val viewModel: EmojiSearchResultViewModel by sharedViewModel()

    private val sharedViewModel: EmojiChooserViewModel by sharedViewModel()
    private lateinit var binding : FragmentGenericRecyclerBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentGenericRecyclerBinding.bind(view)
        epoxyController.listener = this
        binding.recyclerView.configureWith(epoxyController, showDivider = true)
        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            epoxyController.setData(it)
        })
    }

    override fun onDestroyView() {
        epoxyController.listener = null
        binding.recyclerView.cleanup()
        super.onDestroyView()
    }

    override fun onReactionSelected(reaction: String) {
        sharedViewModel.selectedReaction = reaction
        sharedViewModel.navigateEvent.setValue(EmojiChooserViewModel.NAVIGATE_FINISH)
    }

}
