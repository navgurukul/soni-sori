package org.navgurukul.learn.ui.learn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.merakilearn.learn.R
import org.merakilearn.learn.databinding.BatchSelectionSheetBinding
import org.navgurukul.commonui.platform.SpaceItemDecoration
import org.navgurukul.learn.ui.learn.adapter.BatchSelectionAdapter

class LearnBatchSelectionSheet: BottomSheetDialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    private val viewModel: LearnFragmentViewModel by sharedViewModel()
    private lateinit var adapter: BatchSelectionAdapter
    private lateinit var mBinding: BatchSelectionSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.batch_selection_sheet, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val offsetFromTop = resources.getDimensionPixelOffset(R.dimen.sheet_top_offset)
        (dialog as? BottomSheetDialog)?.behavior?.apply {
            setExpandedOffset(offsetFromTop)
        }

        mBinding.tvTitle.text = getString(org.navgurukul.commonui.R.string.more_batch)

        adapter = BatchSelectionAdapter {
            viewModel.selectBatch(it)
        }


        mBinding.apply {
            recyclerView.adapter = adapter
            recyclerView.addItemDecoration(
                SpaceItemDecoration(
                    requireContext().resources.getDimensionPixelSize(
                        org.navgurukul.commonui.R.dimen.spacing_3x
                    ), 0
                )
            )
            recyclerView.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                ).apply {
                    setDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.divider)!!)
                })
        }

        viewModel.viewState.observe(viewLifecycleOwner) {
            adapter.submitList(it.batches.take(3))
        }
        viewModel.viewEvents.observe(
            viewLifecycleOwner,
        ) {
            dismiss()
        }
    }
}


