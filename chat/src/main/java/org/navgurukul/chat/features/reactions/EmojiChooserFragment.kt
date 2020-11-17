package org.navgurukul.chat.features.reactions

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.emoji_chooser_fragment.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.navgurukul.chat.R
import org.navgurukul.chat.core.extensions.cleanup
import org.navgurukul.commonui.platform.BaseFragment

class EmojiChooserFragment : BaseFragment(),
    EmojiRecyclerAdapter.InteractionListener,
    ReactionClickListener {

    override fun getLayoutResId() = R.layout.emoji_chooser_fragment

    private val viewModel: EmojiChooserViewModel by sharedViewModel()

    private val emojiRecyclerAdapter: EmojiRecyclerAdapter by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emojiRecyclerAdapter.reactionClickListener = this
        emojiRecyclerAdapter.interactionListener = this

        emojiRecyclerView.adapter = emojiRecyclerAdapter

        viewModel.moveToSection.observe(viewLifecycleOwner, Observer { section ->
            emojiRecyclerAdapter.scrollToSection(section)
        })
    }

    override fun firstVisibleSectionChange(section: Int) {
        viewModel.setCurrentSection(section)
    }

    override fun onReactionSelected(reaction: String) {
        viewModel.onReactionSelected(reaction)
    }

    override fun onDestroyView() {
        emojiRecyclerView.cleanup()
        emojiRecyclerAdapter.reactionClickListener = null
        emojiRecyclerAdapter.interactionListener = null
        super.onDestroyView()
    }
}
