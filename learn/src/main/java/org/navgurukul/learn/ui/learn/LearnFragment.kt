package org.navgurukul.learn.ui.learn

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.batch_card.*
import kotlinx.android.synthetic.main.fragment_learn.*
import kotlinx.android.synthetic.main.layout_classinfo_dialog.view.*
import kotlinx.android.synthetic.main.upcoming_class_selection_sheet.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.merakilearn.core.navigator.MerakiNavigator
import org.navgurukul.commonui.platform.ToolbarConfigurable
import org.navgurukul.commonui.views.EmptyStateView
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.PathwayCTA
import org.navgurukul.learn.courses.network.model.*
import org.navgurukul.learn.databinding.FragmentLearnBinding
import org.navgurukul.learn.ui.learn.adapter.CourseAdapter
import org.navgurukul.learn.ui.learn.adapter.DotItemDecoration
import org.navgurukul.learn.ui.learn.adapter.UpcomingClassAdapter
import org.navgurukul.learn.ui.learn.adapter.UpcomingEnrolAdapater
import org.navgurukul.learn.util.BrowserRedirectHelper

class LearnFragment : Fragment(){

    private val viewModel: LearnFragmentViewModel by sharedViewModel()
    private lateinit var mCourseAdapter: CourseAdapter
    private lateinit var mBinding: FragmentLearnBinding
    private lateinit var mClassAdapter: UpcomingClassAdapter
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

//        recyclerView()

        viewModel.handle(LearnFragmentViewActions.RequestPageLoad)
//        viewModel.handle(LearnFragmentViewActions.ShowUpcomingClasses)

        viewModel.viewState.observe(viewLifecycleOwner) {
            mBinding.swipeContainer.isRefreshing = false
            mBinding.progressBarButton.isVisible = it.loading
            mCourseAdapter.submitList(it.courses, it.logo)
            configureToolbar(
                it.subtitle,
                it.pathways.isNotEmpty(),
                it.selectedLanguage,
                it.languages.isNotEmpty(),
            )
            mBinding.emptyStateView.isVisible = !it.loading && it.courses.isEmpty()
            mBinding.layoutTakeTest.isVisible = it.showTakeTestButton

            if (it.showTakeTestButton)
                showTestButton(it.pathways[it.currentPathwayIndex].cta!!)
        }

        viewModel.viewEvents.observe(viewLifecycleOwner) {
            when (it) {
                is LearnFragmentViewEvents.OpenCourseDetailActivity -> {
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
                 is LearnFragmentViewEvents.OpenBatchSelectionSheet -> {
                    LearnBatchSelectionSheet().show(
                        parentFragmentManager,
                        "LearnBatchesSelectionSheet"
                    )
                }
                is LearnFragmentViewEvents.BatchSelectClicked ->{
                    showEnrolDialog(it.batch)
                }

                is LearnFragmentViewEvents.ShowUpcomingBatch ->{
                    setUpUpcomingData(it.batch)

                }
                is LearnFragmentViewEvents.ShowUpcomingClasses ->{
                    recyclerView()
                }

//                is LearnFragmentViewEvents.ShowUpcomingClasses ->{
////                    initUpcomingRecyclerView()
////                    setUpcomingClass(it.upcomingClass)
//                }

                is LearnFragmentViewEvents.OpenUrl -> {
                    it.cta?.let { cta ->
                        if (cta.url.contains(BrowserRedirectHelper.WEBSITE_REDIRECT_URL_DELIMITER))
                            merakiNavigator.openCustomTab(
                                BrowserRedirectHelper.getRedirectUrl(requireContext(), cta.url)
                                    .toString(),
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
        }
    }


    fun recyclerView(){
        mClassAdapter = UpcomingClassAdapter()

        val layoutManager= LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL, false)
        recyclerViewUpcoming.layoutManager = layoutManager
        recyclerViewUpcoming.adapter = mClassAdapter
    }

    private fun setUpUpcomingData(batch: Batch) {
        tv_batch_type.text =batch.sanitizedType()
        tv_title_batch.text = batch.title
        tvBtnEnroll.text = batch.title
        tvBatchDate.text = batch.dateRange()

        tvBtnEnroll.setOnClickListener {
            showEnrolDialog(batch)
        }
        more_classe.setOnClickListener {
            viewModel.handle(LearnFragmentViewActions.BtnMoreBatchClicked)
        }
    }

//    private fun setUpcomingClass(upcomingClass:UpcomingClass) {
//        sub_title.text = upcomingClass.sub_title
//        tvTitle.text = upcomingClass.type
//        recycler_view_upcoming.adapter = mClassAdapter
//        return viewModel.getUpcomingClasses()
////         mClassAdapter = UpcomingEnrolAdapater {
////            viewModel.getUpcomingClasses()
////        }
//
//    }

    private fun showTestButton(cta: PathwayCTA) {
        mBinding.buttonTakeTest.text = cta.value
        mBinding.buttonTakeTest.setOnClickListener {
            viewModel.handle(LearnFragmentViewActions.PathwayCtaClicked)
        }
    }

    private fun showEnrolDialog(batch: Batch) {
        val alertLayout: View =  getLayoutInflater().inflate(R.layout.layout_classinfo_dialog, null)
        val btnAccept: View = alertLayout.findViewById(R.id.btnEnroll)
        val btnBack: View = alertLayout.findViewById(R.id.btnback)
        val builder: AlertDialog.Builder = AlertDialog.Builder(this.requireContext())
        builder.setView(alertLayout)
        builder.setCancelable(true)
        val btAlertDialog: AlertDialog? = builder.create()
        btAlertDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        btAlertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvClassTitle = alertLayout.tvClassTitle
        tvClassTitle.text = batch.title
        val tvBatchDate = alertLayout.tv_Batch_Date
        tvBatchDate.text = batch.dateRange()


        btnAccept.setOnClickListener {
            viewModel.handle(LearnFragmentViewActions.PrimaryAction(batch.id))
            btAlertDialog?.dismiss()
        }

        btnBack.setOnClickListener {
            btAlertDialog?.dismiss()
        }

        btAlertDialog?.show()

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

