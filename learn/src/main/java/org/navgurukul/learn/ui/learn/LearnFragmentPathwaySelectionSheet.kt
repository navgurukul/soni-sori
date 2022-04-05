package org.navgurukul.learn.ui.learn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.learn_selection_sheet.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.navgurukul.commonui.platform.SpaceItemDecoration
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.Pathway
import org.navgurukul.learn.databinding.ItemPathwayBinding
import org.navgurukul.learn.ui.common.DataBoundListAdapter

class LearnFragmentPathwaySelectionSheet : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    private val viewModel: LearnFragmentViewModel by sharedViewModel()
    private lateinit var adapter: PathwaySelectionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.learn_selection_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val offsetFromTop = resources.getDimensionPixelSize(R.dimen.sheet_top_offset)
        (dialog as? BottomSheetDialog)?.behavior?.apply {
            setExpandedOffset(offsetFromTop)
        }

        adapter = PathwaySelectionAdapter {
            viewModel.selectPathway(it)
        }
        recycler_view.adapter = adapter
        recycler_view.addItemDecoration(
            SpaceItemDecoration(
                requireContext().resources.getDimensionPixelSize(
                    R.dimen.spacing_3x
                ), 0
            )
        )
        recycler_view.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            ).apply {
                setDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.divider)!!)
            })

        viewModel.viewState.observe(viewLifecycleOwner, {
            adapter.submitList(it.pathways)
        })

        viewModel.viewEvents.observe(viewLifecycleOwner, {
            dismiss()
        })
    }
}

class PathwaySelectionAdapter(val callback: (Pathway) -> Unit) :
    DataBoundListAdapter<Pathway, ItemPathwayBinding>(
        mDiffCallback = object : DiffUtil.ItemCallback<Pathway>() {
            override fun areItemsTheSame(oldItem: Pathway, newItem: Pathway): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Pathway, newItem: Pathway): Boolean {
                return oldItem == newItem
            }
        }
    ) {
    override fun createBinding(parent: ViewGroup, viewType: Int): ItemPathwayBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_pathway, parent, false
        )
    }

    override fun bind(holder: DataBoundViewHolder<ItemPathwayBinding>, item: Pathway) {
        val binding = holder.binding
        binding.pathway = item
        binding.root.setOnClickListener {
            callback.invoke(item)
        }
        val thumbnail = Glide.with(holder.itemView)
            .load(R.drawable.ic_typing_icon)
        Glide.with(binding.ivPathwayIcon)
            .load(item.logo)
            .apply(RequestOptions().override(binding.ivPathwayIcon.resources.getDimensionPixelSize(R.dimen.pathway_select_icon_size)))
            .thumbnail(thumbnail)
            .into(binding.ivPathwayIcon)
    }
}