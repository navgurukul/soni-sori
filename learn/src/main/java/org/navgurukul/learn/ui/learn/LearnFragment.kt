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
import kotlinx.android.synthetic.main.batch_card.*
import kotlinx.android.synthetic.main.layout_classinfo_dialog.view.*
import kotlinx.android.synthetic.main.upcoming_class_selection_sheet.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.merakilearn.core.extentions.setWidthPercent
import org.merakilearn.core.navigator.MerakiNavigator
import org.navgurukul.commonui.platform.ToolbarConfigurable
import org.navgurukul.commonui.views.EmptyStateView
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.ClassType
import org.navgurukul.learn.courses.db.models.CourseClassContent
import org.navgurukul.learn.courses.db.models.PathwayCTA
import org.navgurukul.learn.courses.network.model.*
import org.navgurukul.learn.databinding.FragmentLearnBinding
import org.navgurukul.learn.ui.common.toast
import org.navgurukul.learn.ui.learn.adapter.CourseAdapter
import org.navgurukul.learn.ui.learn.adapter.DotItemDecoration
import org.navgurukul.learn.ui.learn.adapter.UpcomingEnrolAdapater
import org.navgurukul.learn.util.BrowserRedirectHelper
import org.navgurukul.learn.util.toDate

class LearnFragment : Fragment(){

    private val viewModel: LearnFragmentViewModel by sharedViewModel()
    private lateinit var mCourseAdapter: CourseAdapter
    private lateinit var mBinding: FragmentLearnBinding
    private lateinit var mClassAdapter: UpcomingEnrolAdapater
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
//        mBinding.batchCard.root.visibility = View.GONE
//        mBinding.upcoming.root.visibility = View.GONE

        initSwipeRefresh()

        configureToolbar()

        viewModel.handle(LearnFragmentViewActions.RequestPageLoad)
        viewModel.viewState.observe(viewLifecycleOwner) {
            mBinding.swipeContainer.isRefreshing = false
            mBinding.progressBarButton.isVisible = it.loading
            mCourseAdapter.submitList(it.courses, it.logo)
            configureToolbar(
                it.subtitle,
                it.pathways.isNotEmpty(),
                it.selectedLanguage,
                it.languages.isNotEmpty(),
                it.logo
            )
            mBinding.emptyStateView.isVisible = !it.loading && it.courses.isEmpty()
            mBinding.layoutTakeTest.isVisible = it.showTakeTestButton
            if(!it.classes.isEmpty()){
                mBinding.upcoming.root.isVisible = true
                initUpcomingRecyclerView(it.classes)
                mBinding.enrolledButFinished.root.isVisible = false
            }else{
                mBinding.upcoming.root.isVisible = false
            }
            if(!it.batches.isEmpty()){
                mBinding.batchCard.root.isVisible = true
                setUpUpcomingData(it.batches.first())
            }else{
                mBinding.batchCard.root.isVisible = false
            }

            if (it.showTakeTestButton)
                showTestButton(it.pathways[it.currentPathwayIndex].cta!!)
//            updateState(it)
        }

        viewModel.viewEvents.observe(viewLifecycleOwner) {
            when (it) {
                is LearnFragmentViewEvents.OpenCourseDetailActivity -> {
                    CourseContentActivity.start(requireContext(), it.courseId, it.pathwayId)
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
                    mBinding.batchCard.root.visibility = View.VISIBLE
                    mBinding.upcoming.root.visibility = View.GONE
                }
                is LearnFragmentViewEvents.ShowUpcomingClasses ->{
                    initUpcomingRecyclerView(it.classes)
                    mBinding.upcoming.root.visibility = View.VISIBLE
                    mBinding.batchCard.root.visibility = View.GONE
                    mBinding.enrolledButFinished.root.visibility = View.GONE

                }
                is LearnFragmentViewEvents.ShowCompletedStatus ->{
                    mBinding.enrolledButFinished.root.visibility = View.VISIBLE
                    mBinding.upcoming.root.visibility = View.GONE
                }


                is LearnFragmentViewEvents.ShowToast -> toast(it.toastText)
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

    private fun setUpUpcomingData(batch: Batch) {
        tvType.text =batch.sanitizedType()+"  :"
        tvTitleBatch.text = batch.title
        tvBtnEnroll.text = batch.title
        tvBatchDate.text = batch.dateRange()
        tvText.text = "Can't start on "+ batch.startTime?.toDate()
        tvBtnEnroll.setOnClickListener {
            showEnrolDialog(batch)
        }
        more_classe.setOnClickListener {
            viewModel.handle(LearnFragmentViewActions.BtnMoreBatchClicked)
        }
    }

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
            initSwipeRefresh()
            viewModel.handle(LearnFragmentViewActions.PrimaryAction(batch.id?:0))
            btAlertDialog?.dismiss()
        }
        btnBack.setOnClickListener {
            btAlertDialog?.dismiss()
        }
        btAlertDialog?.show()
        btAlertDialog?.setWidthPercent(45);
    }

    private fun configureToolbar(
        subtitle: String? = null, attachClickListener: Boolean = false,
        selectedLanguage: String? = null, languageClickListener: Boolean = false, pathwayIcon : String? = null
    ) {
        (activity as? ToolbarConfigurable)?.let {
            if (subtitle != null) {
                it.configure(
                    subtitle,
                    R.attr.textPrimary,
                    subtitle="",
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
                    showPathwayIcon = true,
                    pathwayIcon = pathwayIcon
                )
            }
        }
    }


    private fun initSwipeRefresh() {
        mBinding.swipeContainer.setOnRefreshListener {
            viewModel.handle(LearnFragmentViewActions.RefreshCourses)
            mBinding.swipeContainer.isRefreshing = false
        }
    }

    private fun initUpcomingRecyclerView(upcomingClassList: List<CourseClassContent>){
        mClassAdapter = UpcomingEnrolAdapater{
            val viewState = viewModel.viewState.value
            viewState?.let { state ->
                val pathwayId = state.pathways[state.currentPathwayIndex].id
                if(it.type == ClassType.doubt_class)
                    ClassActivity.start(requireContext(), it)
                else if(it.type == ClassType.revision)
                    CourseContentActivity.start(requireContext(), it.parentId?:it.courseId, pathwayId, it.id)
                else{
                    CourseContentActivity.start(requireContext(), it.courseId, pathwayId, it.id)
                }
            }
        }
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewUpcoming.layoutManager = layoutManager
        recyclerViewUpcoming.adapter = mClassAdapter

        mClassAdapter.submitList(upcomingClassList)
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
