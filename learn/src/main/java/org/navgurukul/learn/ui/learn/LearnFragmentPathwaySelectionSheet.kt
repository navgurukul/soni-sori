package org.navgurukul.learn.ui.learn

import android.content.Context
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
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.navgurukul.commonui.platform.SpaceItemDecoration
import org.navgurukul.commonui.platform.SvgLoader
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.Pathway
import org.navgurukul.learn.databinding.ItemPathwayBinding
import org.navgurukul.learn.databinding.LearnSelectionSheetBinding
import org.navgurukul.learn.ui.common.DataBoundListAdapter

class LearnFragmentPathwaySelectionSheet : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    private val viewModel: LearnFragmentViewModel by sharedViewModel()
    private lateinit var adapter: PathwaySelectionAdapter
    private  lateinit var binding: LearnSelectionSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LearnSelectionSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding ?: return

        val offsetFromTop = resources.getDimensionPixelSize(R.dimen.sheet_top_offset)
        (dialog as? BottomSheetDialog)?.behavior?.apply {
            setExpandedOffset(offsetFromTop)
        }

        adapter = PathwaySelectionAdapter(requireContext()) {
            viewModel.selectPathway(it)
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

        viewModel.viewState.observe(viewLifecycleOwner, { state ->
            val filteredPathways = state.pathways.filter { it.platform == "both" }
            adapter.submitList(filteredPathways)
        })

        viewModel.viewEvents.observe(viewLifecycleOwner, {
            dismiss()
        })
    }
}

class PathwaySelectionAdapter( val context: Context,  val callback: (Pathway) -> Unit) :
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

        if (item.logo?.endsWith(".svg") == true) {
            SvgLoader(context).loadSvgFromUrl(item.logo, binding.ivPathwayIcon)
        }
        else {
            val thumbnail = Glide.with(holder.itemView)
                .load(R.drawable.ic_typing_icon)
            Glide.with(binding.ivPathwayIcon)
                .load(item.logo)
                .apply(RequestOptions().override(binding.ivPathwayIcon.resources.getDimensionPixelSize(R.dimen.pathway_select_icon_size)))
                .thumbnail(thumbnail)
                .into(binding.ivPathwayIcon)
        }
    }
}
