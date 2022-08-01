package org.navgurukul.learn.ui.learn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.batch_selection_sheet.recycler_view
import kotlinx.android.synthetic.main.batch_selection_sheet.tv_title
import kotlinx.android.synthetic.main.learn_selection_sheet.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.navgurukul.commonui.platform.SpaceItemDecoration
import org.navgurukul.learn.R
import org.navgurukul.learn.ui.learn.adapter.BatchSelectionAdapter

class LearnBatchSelectionSheet: BottomSheetDialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    private val viewModel: LearnFragmentViewModel by sharedViewModel()
    private lateinit var adapter: BatchSelectionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.batch_selection_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val offsetFromTop = resources.getDimensionPixelOffset(R.dimen.sheet_top_offset)
        (dialog as? BottomSheetDialog)?.behavior?.apply {
            setExpandedOffset(offsetFromTop)
        }

        tv_title.text = getString(R.string.more_batch)

        adapter = BatchSelectionAdapter {
            viewModel.selectBatch(it)
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


