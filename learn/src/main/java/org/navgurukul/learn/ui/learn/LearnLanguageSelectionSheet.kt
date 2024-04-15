package org.navgurukul.learn.ui.learn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.merakilearn.core.datasource.model.Language
import org.navgurukul.commonui.platform.SpaceItemDecoration
import org.navgurukul.learn.R
import org.navgurukul.learn.databinding.ItemLanguageBinding
import org.navgurukul.learn.databinding.LearnSelectionSheetBinding
import org.navgurukul.learn.ui.common.DataBoundListAdapter

class LearnLanguageSelectionSheet : BottomSheetDialogFragment() {
lateinit var binding: LearnSelectionSheetBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    private val viewModel: LearnFragmentViewModel by sharedViewModel()
    private lateinit var adapter: LanguageSelectionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= LearnSelectionSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val offsetFromTop = resources.getDimensionPixelSize(R.dimen.sheet_top_offset)
        (dialog as? BottomSheetDialog)?.behavior?.apply {
            setExpandedOffset(offsetFromTop)
        }

        binding.tvTitle.text = getString(R.string.select_language)

        adapter = LanguageSelectionAdapter {
            viewModel.selectLanguage(it)
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(
            SpaceItemDecoration(
                requireContext().resources.getDimensionPixelSize(
                    R.dimen.spacing_3x
                ), 0
            )
        )
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            ).apply {
                setDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.divider)!!)
            })

        viewModel.viewState.observe(viewLifecycleOwner) {
            adapter.submitList(it.languages)
        }

        viewModel.viewEvents.observe(viewLifecycleOwner, {
            dismiss()
        })
    }
}

class LanguageSelectionAdapter(val callback: (Language) -> Unit) :
    DataBoundListAdapter<Language, ItemLanguageBinding>(
        mDiffCallback = object : DiffUtil.ItemCallback<Language>() {
            override fun areItemsTheSame(oldItem: Language, newItem: Language): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Language, newItem: Language): Boolean {
                return oldItem == newItem
            }
        }
    ) {
    override fun createBinding(parent: ViewGroup, viewType: Int): ItemLanguageBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_language, parent, false
        )
    }

    override fun bind(holder: DataBoundViewHolder<ItemLanguageBinding>, item: Language) {
        val binding = holder.binding
        binding.language = item
        binding.root.setOnClickListener {
            callback.invoke(item)
        }
    }

}
