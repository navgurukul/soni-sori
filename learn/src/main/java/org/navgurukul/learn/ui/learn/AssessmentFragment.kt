package org.navgurukul.learn.ui.learn

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.incorrect_output_layout.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.merakilearn.core.extentions.fragmentArgs
import org.merakilearn.core.extentions.toBundle
import org.navgurukul.commonui.platform.SpaceItemDecoration
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.BaseCourseContent
import org.navgurukul.learn.courses.db.models.CourseContentType
import org.navgurukul.learn.courses.db.models.OptionResponse
import org.navgurukul.learn.courses.db.models.OptionsBaseCourseContent
import org.navgurukul.learn.courses.network.AttemptResponse
import org.navgurukul.learn.courses.network.AttemptStatus
import org.navgurukul.learn.databinding.FragmentAssessmentBinding
import org.navgurukul.learn.ui.common.toast
import org.navgurukul.learn.ui.learn.adapter.ExerciseContentAdapter
import org.navgurukul.learn.ui.learn.viewholder.AssessmentFragmentViewModel


class AssessmentFragment : Fragment() {

    private val args: CourseContentArgs by fragmentArgs()
    private lateinit var mBinding: FragmentAssessmentBinding
    private lateinit var contentAdapter: ExerciseContentAdapter
    private var isContentRvClickable = true
    private lateinit var correctAdapter: ExerciseContentAdapter
    private lateinit var inCorrectAdapter: ExerciseContentAdapter
    private var selectedOptions: List<OptionResponse>? = null
    private val fragmentViewModel: AssessmentFragmentViewModel by viewModel(parameters = {
        parametersOf(args)
    })
    private lateinit var activityViewModel: CourseContentActivityViewModel

    companion object {
        fun newInstance(
            isFirst: Boolean,
            isLast: Boolean,
            isCompleted: Boolean,
            courseId: String,
            assessmentId: String,
            courseContentType: CourseContentType,
            pathwayId : Int
        ): AssessmentFragment {
            return AssessmentFragment().apply {
                arguments = CourseContentArgs(
                    isFirst,
                    isLast,
                    isCompleted,
                    courseId,
                    assessmentId,
                    courseContentType,
                    pathwayId
                ).toBundle()
            }
        }

        const val TAG = "AssessmentFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_assessment, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.btnSubmit.visibility = View.GONE
        mBinding.correctOutputLayout.root.visibility = View.GONE
        mBinding.incorrectOutputLayout.visibility = View.GONE

        activityViewModel =
            ViewModelProvider(requireActivity()).get(CourseContentActivityViewModel::class.java)

        initContentRv()
        fragmentViewModel.viewEvents.observe(viewLifecycleOwner) {
            when (it) {
                is AssessmentFragmentViewModel.AssessmentFragmentViewEvents.ShowToast -> toast(it.toastText)
                is AssessmentFragmentViewModel.AssessmentFragmentViewEvents.ShowCorrectOutput -> {
                    isContentRvClickable = false
                    initCorrectRV(it.list)
                    mBinding.correctOutputLayout.root.visibility = View.VISIBLE
                    mBinding.incorrectOutputLayout.visibility = View.GONE
                }
                is AssessmentFragmentViewModel.AssessmentFragmentViewEvents.ShowRetryOnce -> {
                    mBinding.incorrectOutputLayout.visibility = View.VISIBLE
                    mBinding.correctOutputLayout.root.visibility = View.GONE
                    isContentRvClickable = false
                    setupIncorrectOutputLayout(it.list, it.attemptResponse)
                }
                is AssessmentFragmentViewModel.AssessmentFragmentViewEvents.ShowIncorrectOutput -> {
                    mBinding.incorrectOutputLayout.visibility = View.VISIBLE
                    mBinding.correctOutputLayout.root.visibility = View.GONE
                    initIncorrectRV(it.list)
                    isContentRvClickable = false

                }
                is AssessmentFragmentViewModel.AssessmentFragmentViewEvents.ShowPartiallyCorrectOutput, is AssessmentFragmentViewModel.AssessmentFragmentViewEvents.ShowPartiallyIncorrectOutput -> {
                    mBinding.incorrectOutputLayout.visibility = View.VISIBLE
                    mBinding.correctOutputLayout.root.visibility = View.GONE
                    initIncorrectRV(it as List<BaseCourseContent>)
                    isContentRvClickable = false
                }

            }
        }
        fragmentViewModel.viewState.observe(viewLifecycleOwner) {
            mBinding.progressBar.visibility = if (it.isLoading) View.VISIBLE else View.GONE

            showErrorScreen(it.isError)

            if (!it.isError)
                contentAdapter.submitList(getNewReferencedList(it.assessmentContentListForUI))

        }
        initScreenRefresh()

    }

    private fun showErrorScreen(isError: Boolean) {
        if (isError) {
            mBinding.errorLayout.root.visibility = View.VISIBLE
            mBinding.contentLayout.visibility = View.GONE
        } else {
            mBinding.errorLayout.root.visibility = View.GONE
            mBinding.contentLayout.visibility = View.VISIBLE
        }
    }

    private fun setUpSubmitAnswer() {
        mBinding.btnSubmit.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                mBinding.btnSubmit.visibility = View.GONE
                selectedOptions?.let {
                    isContentRvClickable = false
                    fragmentViewModel.handle(
                        AssessmentFragmentViewModel.AssessmentFragmentViewActions.SubmitOptionClicked(
                            it
                        )
                    )
                    activityViewModel.handle(CourseContentActivityViewActions.ContentMarkedCompleted)
                }
            }
            initScreenRefresh()
        }
    }

    private fun setupIncorrectOutputLayout(
        list: List<BaseCourseContent>,
        attemptResponse: AttemptResponse?
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            if (attemptResponse != null) {
                if (attemptResponse.attemptCount < 2) {
                    if (attemptResponse.attemptStatus == AttemptStatus.PARTIALLY_CORRECT){
                        mBinding.incorrectOutputLayout.btnRetry.visibility = View.VISIBLE
                        mBinding.incorrectOutputLayout.miss_txt.text = "\uD83D\uDE2F Quite close! However, some correct answer(s) were missed"
                        mBinding.incorrectOutputLayout.btnRetry.setOnClickListener {
                            isContentRvClickable = true
                            mBinding.incorrectOutputLayout.visibility = View.GONE
                            fragmentViewModel.handle(AssessmentFragmentViewModel.AssessmentFragmentViewActions.ShowUpdatedOutput)
                        }
                    }else if(attemptResponse.attemptStatus == AttemptStatus.PARTIALLY_INCORRECT){
                        mBinding.incorrectOutputLayout.miss_txt.text = "\uD83D\uDE2F Quite close! However, both correct and incorrect answers were selected"
                        mBinding.incorrectOutputLayout.btnRetry.visibility = View.VISIBLE
                        mBinding.incorrectOutputLayout.btnRetry.setOnClickListener {
                            isContentRvClickable = true
                            mBinding.incorrectOutputLayout.visibility = View.GONE
                            fragmentViewModel.handle(AssessmentFragmentViewModel.AssessmentFragmentViewActions.ShowUpdatedOutput)
                        }
                    }else if (attemptResponse.attemptStatus == AttemptStatus.INCORRECT){
                        mBinding.incorrectOutputLayout.btnRetry.visibility = View.VISIBLE
                        mBinding.incorrectOutputLayout.btnRetry.setOnClickListener {
                            isContentRvClickable = true
                            mBinding.incorrectOutputLayout.visibility = View.GONE
                            fragmentViewModel.handle(AssessmentFragmentViewModel.AssessmentFragmentViewActions.ShowUpdatedOutput)
                        }
                    }
                } else {
                    mBinding.incorrectOutputLayout.btnRetry.visibility = View.GONE
                    mBinding.incorrectOutputLayout.incorrectRv.isVisible = true
                    mBinding.incorrectOutputLayout.explanationRetryLayout.visibility = View.GONE
                    fragmentViewModel.handle(AssessmentFragmentViewModel.AssessmentFragmentViewActions.ShowCorrectOnIncorrect)
                    initIncorrectRV(list)
                    isContentRvClickable = false
                }
            }
        }
    }

    private fun getNewReferencedList(list: List<BaseCourseContent>?): List<BaseCourseContent>? {
        val newList = list?.toMutableList()?.map {
            if (it.component == BaseCourseContent.COMPONENT_OPTIONS) {
                (it as OptionsBaseCourseContent).copy(
                    value = it.value.toMutableList().map { it.copy() }
                )
            } else {
                it
            }
        }
        return newList
    }

    private fun initScreenRefresh() {
        mBinding.swipeContainer.setOnRefreshListener {
            fragmentViewModel.handle(AssessmentFragmentViewModel.AssessmentFragmentViewActions.RequestContentRefresh)
            mBinding.swipeContainer.isRefreshing = false
        }

        mBinding.errorLayout.btnRefresh.setOnClickListener {
            fragmentViewModel.handle(AssessmentFragmentViewModel.AssessmentFragmentViewActions.RequestContentRefresh)
        }
    }

    private fun initContentRv() {
        contentAdapter = ExerciseContentAdapter(this.requireContext(), {

        }, {

        }, {
            Log.d(TAG, "initContentRvbefore: $isContentRvClickable")
            if (isContentRvClickable) {
                Log.d(TAG, "initContentRv: $isContentRvClickable")
                selectedOptions = it
                fragmentViewModel.handle(
                    AssessmentFragmentViewModel.AssessmentFragmentViewActions.OptionSelected(
                        it
                    )
                )
                mBinding.btnSubmit.visibility = View.VISIBLE
            }
        })
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mBinding.recyclerViewAsses.layoutManager = layoutManager
        mBinding.recyclerViewAsses.adapter = contentAdapter
        mBinding.recyclerViewAsses.addItemDecoration(
            SpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.spacing_1x), 0)
        )
        setUpSubmitAnswer()
    }

    private fun initCorrectRV(list: List<BaseCourseContent>) {
        correctAdapter = ExerciseContentAdapter(this.requireContext(), {}, {}, {})
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mBinding.correctOutputLayout.outputLayout.layoutManager = layoutManager
        mBinding.correctOutputLayout.outputLayout.adapter = correctAdapter
        correctAdapter.submitList(getNewReferencedList(list))
    }

    private fun initIncorrectRV(list: List<BaseCourseContent>) {
        inCorrectAdapter = ExerciseContentAdapter(this.requireContext(), {}, {}, {})
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mBinding.incorrectOutputLayout.incorrectRv.layoutManager = layoutManager
        mBinding.incorrectOutputLayout.incorrectRv.adapter = inCorrectAdapter
        inCorrectAdapter.submitList(getNewReferencedList(list))
    }
}
