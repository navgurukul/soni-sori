package org.navgurukul.learn.ui.learn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_exercise.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.merakilearn.core.navigator.MerakiNavigator
import org.navgurukul.commonui.platform.ToolbarConfigurable
import org.navgurukul.commonui.views.EmptyStateView
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.Course
import org.navgurukul.learn.courses.db.models.PathwayCTA
import org.navgurukul.learn.databinding.FragmentLearnBinding
import org.navgurukul.learn.ui.learn.adapter.CourseAdapter
import org.navgurukul.learn.ui.learn.adapter.DotItemDecoration
import org.navgurukul.learn.util.BrowserRedirectHelper

class LearnFragment : Fragment(){

    private val viewModel: LearnFragmentViewModel by sharedViewModel()
    private lateinit var mCourseAdapter: CourseAdapter
    private lateinit var mBinding: FragmentLearnBinding
    private val merakiNavigator: MerakiNavigator by inject()

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

        initSwipeRefresh()

        configureToolbar()

//        mBinding.tvMeaningfulWordTyping.setOnClickListener {
//            ExerciseWordActivity.start(requireContext(), "2000", 61)
//
//        }
        viewModel.viewState.observe(viewLifecycleOwner, {
            mBinding.swipeContainer.isRefreshing = false
            mBinding.progressBarButton.isVisible = it.loading
            val mutableCoursesList = it.courses as MutableList

            //TODO: Remove Hardcoding later
            if(mutableCoursesList.isNotEmpty() && mutableCoursesList[0].id != "2001")
                mutableCoursesList.add(0, Course(id= "2001", name= "Paragraph (Typing)", pathwayId=61, shortDescription="Let's learn typing", supportedLanguages= emptyList()))

            mCourseAdapter.submitList(mutableCoursesList, it.logo)
            configureToolbar(
                it.subtitle,
                it.pathways.isNotEmpty(),
                it.selectedLanguage,
                it.languages.isNotEmpty()
            )
            mBinding.emptyStateView.isVisible = !it.loading && it.courses.isEmpty()
            mBinding.layoutTakeTest.isVisible = it.showTakeTestButton

            if(it.showTakeTestButton)
                showTestButton(it.pathways[it.currentPathwayIndex].cta!!)
        })

        viewModel.viewEvents.observe(viewLifecycleOwner, {
            when (it) {
                is LearnFragmentViewEvents.OpenCourseDetailActivity -> {
                    //TODO: Will remove after API integration
                    if(it.courseId == "2001") {
                        merakiNavigator.openDeepLink(requireActivity()
                            , "https://merakilearn.org/typing"
                    , "{\"content\":[\"hello, again, friend of a friend, I knew you when\", \"our common goal was waiting for, the world to end.\"],\"code\":\"wordstyping\"}")
                    }
                    else
                    ExerciseActivity.start(requireContext(), it.courseId, it.pathwayId)
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
                is LearnFragmentViewEvents.OpenUrl -> {
                    it.cta?.let{ cta ->
                        if(cta.url.contains(BrowserRedirectHelper.WEBSITE_REDIRECT_URL_DELIMITER))
                            merakiNavigator.openCustomTab(
                                BrowserRedirectHelper.getRedirectUrl(requireContext(), cta.url).toString(),
                                requireContext()
                            )
                        else
                            merakiNavigator.openDeepLink(
                                requireActivity(),
                                cta.url
                            )
                    }
                }
                else -> {
                }
            }
        })
    }

    private fun showTestButton(cta: PathwayCTA) {
        mBinding.buttonTakeTest.text = cta.value
        mBinding.buttonTakeTest.setOnClickListener {
            viewModel.handle(LearnFragmentViewActions.PathwayCtaClicked)
        }
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
            DotItemDecoration(requireContext())
        )
    }

}