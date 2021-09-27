package org.navgurukul.learn.ui.learn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.navgurukul.commonui.platform.SpaceItemDecoration
import org.navgurukul.commonui.platform.ToolbarConfigurable
import org.navgurukul.commonui.views.EmptyStateView
import org.navgurukul.learn.R
import org.navgurukul.learn.databinding.FragmentLearnBinding
import org.navgurukul.learn.ui.learn.adapter.CourseAdapter
import org.navgurukul.learn.util.LearnPreferences


class LearnFragment : Fragment() {

    private val viewModel: LearnFragmentViewModel by sharedViewModel()
    private lateinit var mCourseAdapter: CourseAdapter
    private lateinit var mBinding: FragmentLearnBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_learn, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        mBinding.progressBarButton.visibility = View.VISIBLE
        mBinding.emptyStateView.state = EmptyStateView.State.NO_CONTENT

        if(arguments?.getInt("PATHWAY_ID")!=null) {
            viewModel.setPreferenceForCourse(arguments?.getInt("PATHWAY_ID")!!)
        }

        initSwipeRefresh()

        configureToolbar()

        viewModel.viewState.observe(viewLifecycleOwner, {
            mBinding.swipeContainer.isRefreshing = false
            mBinding.progressBarButton.isVisible = it.loading
            mCourseAdapter.submitList(it.courses)
            configureToolbar(
                it.subtitle,
                it.pathways.isNotEmpty(),
                it.selectedLanguage,
                it.languages.isNotEmpty()
            )
            mBinding.emptyStateView.isVisible = !it.loading && it.courses.isEmpty()
        })

        viewModel.viewEvents.observe(viewLifecycleOwner, {
            when (it) {
                is LearnFragmentViewEvents.OpenCourseDetailActivity -> {
                    CourseDetailActivity.start(requireContext(), it.courseId, it.courseName)
                }
                is LearnFragmentViewEvents.OpenCourseSlugActivity -> {
                    CourseSlugDetailActivity.start(requireContext(), it.currentStudy)
                }
                LearnFragmentViewEvents.OpenPathwaySelectionSheet -> {
                    LearnFragmentPathwaySelectionSheet().show(
                        parentFragmentManager,
                        "OpenPathwaySelectionSheet"
                    )
                }
                LearnFragmentViewEvents.OpenLanguageSelectionSheet -> {
                    LearnLanguageSelectionSheet().show(
                        parentFragmentManager,
                        "OpenLanguageSelectionSheet"
                    )
                }
                else -> {}
            }
        })
    }

    private fun configureToolbar(
        subtitle: String? = null, attachClickListener: Boolean = false,
        selectedLanguage: String? = null, languageClickListener: Boolean = false
    ) {
        (activity as? ToolbarConfigurable)?.let {
            it.configure(
                getString(R.string.courses),
                R.attr.textPrimary,
                subtitle = subtitle,
                onClickListener = if (attachClickListener) {
                    {
                        viewModel.handle(LearnFragmentViewActions.ToolbarClicked)
                    }
                } else null,
                action = selectedLanguage,
                actionOnClickListener = if (languageClickListener) {
                    {
                        viewModel.handle(LearnFragmentViewActions.LanguageSelectionClicked)
                    }
                } else null,
            )
        }
    }

    private fun initSwipeRefresh() {
        mBinding.swipeContainer.setOnRefreshListener {
            viewModel.handle(LearnFragmentViewActions.RefreshCourses)
        }
    }

    private fun initRecyclerView() {
        mCourseAdapter = CourseAdapter {
            viewModel.selectCourse(it)
        }
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mBinding.recyclerviewCourse.layoutManager = layoutManager
        mBinding.recyclerviewCourse.adapter = mCourseAdapter
        mBinding.recyclerviewCourse.addItemDecoration(
            SpaceItemDecoration(
                requireContext().resources.getDimensionPixelSize(
                    R.dimen.spacing_3x
                ), 0
            )
        )
    }
}