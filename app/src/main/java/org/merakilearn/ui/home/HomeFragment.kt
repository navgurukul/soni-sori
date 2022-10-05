package org.merakilearn.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.merakilearn.EnrollActivity
import org.merakilearn.R
import org.merakilearn.core.extentions.enableChildren
import org.merakilearn.core.datasource.model.Language
import org.navgurukul.commonui.platform.GridSpacingDecorator
import org.navgurukul.commonui.platform.ToolbarConfigurable
import org.navgurukul.commonui.skeleton.RecyclerViewSkeletonScreen
import org.navgurukul.commonui.skeleton.SkeletonItem
import org.navgurukul.commonui.skeleton.SkeletonScreen
import org.navgurukul.commonui.views.EmptyStateView


class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModel()
    private lateinit var adapter: ClassesAdapter

    private lateinit var skeleton: SkeletonScreen

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onResume() {
        super.onResume()
        (activity as? ToolbarConfigurable)?.configure(
            getString(R.string.app_name),
            R.attr.colorPrimary,
            true
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupEnrolledSwitch()
        setupObserver()
        initSearchListener()
    }

    private fun setupEnrolledSwitch() {
        switch_only_enrolled.setOnCheckedChangeListener { v, checked ->
            viewModel.handle(HomeViewActions.FilterEnrolled(checked))
        }
    }

    private fun setupRecyclerView() {
        adapter = ClassesAdapter(requireContext()) {
            EnrollActivity.start(requireContext(), it.id, it.enrolled)
        }
        recycler_view.adapter = adapter
        val padding = resources.getDimensionPixelSize(R.dimen.spacing_2x)
        recycler_view.addItemDecoration(GridSpacingDecorator(padding, padding, 2, false))
        recycler_view.layoutManager = GridLayoutManager(requireContext(), 2).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when ((recycler_view.adapter as RecyclerView.Adapter<RecyclerView.ViewHolder>).getItemViewType(
                        position
                    )) {
                        ClassesItemTypes.HEADER.ordinal -> 2
                        else -> 1
                    }
                }
            }
        }

        skeleton = RecyclerViewSkeletonScreen(
            recycler_view,
            adapter,
            listOf(
                SkeletonItem(R.layout.classes_header_skeleton, ClassesItemTypes.HEADER.ordinal),
                SkeletonItem(R.layout.classes_item_skeleton, ClassesItemTypes.CLASS.ordinal),
                SkeletonItem(R.layout.classes_item_skeleton, ClassesItemTypes.CLASS.ordinal)
            )
        )
    }

    private fun setupObserver() {
        viewModel.viewState.observe(viewLifecycleOwner, {
            if (it.isLoading) {
                skeleton.show()
            } else {
                skeleton.hide()
            }
            adapter.submitList(it.itemList)
            search_view.enableChildren(it.searchEnabled)
            if (it.showError || it.showNoContent) {
                emptyStateView.isVisible = true
                emptyStateView.state =
                    if (it.showNoContent) EmptyStateView.State.NO_CONTENT else EmptyStateView.State.ERROR
            } else {
                emptyStateView.isVisible = false
            }
        })

        viewModel.supportedLanguages.observe(viewLifecycleOwner, {
            setupChipGroup(it)
        })
    }

    private fun initSearchListener() {
        search_view.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.handle(HomeViewActions.Query(query))
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.handle(HomeViewActions.Query(newText))

                return false
            }
        })
    }

    private fun setupChipGroup(languageList: List<Language>) {
        if (language_chip_group.childCount > 0) {
            language_chip_group.removeAllViews()
        }

        if (languageList.isEmpty()) {
            language_chip_group.isVisible = false
        } else {
            language_chip_group.isVisible = true
            for (index in languageList) {
                val chip = Chip(requireContext())
                chip.text = index.label
                chip.tag = index.code
                chip.setTextColor(
                    AppCompatResources.getColorStateList(
                        requireContext(),
                        R.color.language_chip_text_color
                    )
                )
                chip.chipBackgroundColor = AppCompatResources.getColorStateList(
                    requireContext(),
                    R.color.language_chip_background_color
                )
                chip.chipStrokeColor =
                    AppCompatResources.getColorStateList(
                        requireContext(),
                        R.color.language_chip_border_color
                    )
                chip.setChipStrokeWidthResource(R.dimen.chip_border_width)
                chip.isClickable = true
                chip.isCheckable = true
                chip.checkedIcon = null
                language_chip_group.addView(chip)
            }
            language_chip_group.setOnCheckedChangeListener { group, checkedId ->
                if (checkedId == View.NO_ID) {
                    viewModel.handle(HomeViewActions.FilterLanguage(null))
                    return@setOnCheckedChangeListener
                }
                val chip: Chip = language_chip_group.findViewById(checkedId)
                viewModel.handle(HomeViewActions.FilterLanguage((chip.tag.toString())))
            }
        }
    }
}