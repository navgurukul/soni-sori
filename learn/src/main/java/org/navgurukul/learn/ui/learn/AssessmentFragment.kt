package org.navgurukul.learn.ui.learn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.incorrect_output_layout.*
import kotlinx.android.synthetic.main.incorrect_output_layout.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.merakilearn.core.extentions.fragmentArgs
import org.merakilearn.core.extentions.toBundle
import org.navgurukul.commonui.platform.SpaceItemDecoration
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.*
import org.navgurukul.learn.courses.network.AttemptResponse
import org.navgurukul.learn.databinding.FragmentAssessmentBinding
import org.navgurukul.learn.ui.common.toast
import org.navgurukul.learn.ui.learn.adapter.*
import org.navgurukul.learn.ui.learn.viewholder.AssessmentFragmentViewModel


class AssessmentFragment : Fragment() {

    private val args: CourseContentArgs by fragmentArgs()
    private lateinit var mBinding: FragmentAssessmentBinding
    private lateinit var contentAdapter: ExerciseContentAdapter
    private var isContentRvClickable = true
    private lateinit var correctAdapter: ExerciseContentAdapter
    private lateinit var inCorrectAdapter : ExerciseContentAdapter
    private var selectedOption : OptionResponse? = null
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
            assessmentId : String,
            courseContentType: CourseContentType,
        ): AssessmentFragment {
            return AssessmentFragment().apply {
                arguments = CourseContentArgs(
                    isFirst,
                    isLast,
                    isCompleted,
                    courseId,
                    assessmentId,
                    courseContentType
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_assessment, container , false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.btnSubmit.visibility = View.GONE
        mBinding.correctOutputLayout.root.visibility = View.GONE
        mBinding.incorrectOutputLayout.visibility = View.GONE

        activityViewModel = ViewModelProvider(requireActivity()).get(CourseContentActivityViewModel::class.java)

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
                    setupIncorrectOutputLayout(it.list, it.attemptResponse)
                }
                is AssessmentFragmentViewModel.AssessmentFragmentViewEvents.ShowIncorrectOutput->{
                    mBinding.incorrectOutputLayout.visibility = View.VISIBLE
                    mBinding.correctOutputLayout.root.visibility = View.GONE
                    initIncorrectRV(it.list)
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

    private fun setUpSubmitAnswer(){
        mBinding.btnSubmit.setOnClickListener{
            mBinding.btnSubmit.visibility = View.GONE
            selectedOption?.let {
                isContentRvClickable = false
                fragmentViewModel.handle(AssessmentFragmentViewModel.AssessmentFragmentViewActions.SubmitOptionClicked(it))
                activityViewModel.handle(CourseContentActivityViewActions.ContentMarkedCompleted)
            }
            }
        }

    private fun setupIncorrectOutputLayout(list: List<BaseCourseContent>, attemptResponse: AttemptResponse?) {
        mBinding.incorrectOutputLayout.btnSeeExplanation.setOnClickListener {
//            selectedOption?.let {
//                isContentRvClickable = false
//                fragmentViewModel.handle(AssessmentFragmentViewModel.AssessmentFragmentViewActions.SubmitOptionClicked(it))
//                activityViewModel.handle(CourseContentActivityViewActions.ContentMarkedCompleted)
//            }
            mBinding.incorrectOutputLayout.incorrectRv.isVisible = true
            mBinding.incorrectOutputLayout.explanationRetryLayout.visibility = View.GONE
            initIncorrectRV(list)
            isContentRvClickable = false
            fragmentViewModel.handle(AssessmentFragmentViewModel.AssessmentFragmentViewActions.ShowCorrectOnIncorrect)

        }
        if (attemptResponse != null) {
            if (attemptResponse.attemptCount < 2){
                mBinding.incorrectOutputLayout.btnRetry.visibility = View.VISIBLE
                mBinding.incorrectOutputLayout.btnRetry.setOnClickListener {
                    isContentRvClickable = true
                    mBinding.incorrectOutputLayout.visibility =  View.GONE
                    fragmentViewModel.handle(AssessmentFragmentViewModel.AssessmentFragmentViewActions.ShowUpdatedOutput)
                }
            } else{
                mBinding.incorrectOutputLayout.btnRetry.visibility = View.GONE
                mBinding.incorrectOutputLayout.incorrectRv.isVisible = true
                mBinding.incorrectOutputLayout.explanationRetryLayout.visibility = View.GONE
                fragmentViewModel.handle(AssessmentFragmentViewModel.AssessmentFragmentViewActions.ShowCorrectOnIncorrect)
                initIncorrectRV(list)
                isContentRvClickable = false
            }
        }

    }

    private fun getNewReferencedList(list: List<BaseCourseContent>?): List<BaseCourseContent>? {
        val newList = list?.toMutableList()?.map {
            if(it.component == BaseCourseContent.COMPONENT_OPTIONS){
                (it as OptionsBaseCourseContent).copy(
                    value = it.value.toMutableList().map{ it.copy() }
                )
            }else{
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

    private fun initContentRv(){
        contentAdapter = ExerciseContentAdapter(this.requireContext(),{

        }, {

        } ,{
            if(isContentRvClickable ) {
                selectedOption = it
                fragmentViewModel.handle(AssessmentFragmentViewModel.AssessmentFragmentViewActions.OptionSelected(it))
                mBinding.btnSubmit.visibility = View.VISIBLE
            }
        })
        val layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        mBinding.recyclerViewAsses.layoutManager = layoutManager
        mBinding.recyclerViewAsses.adapter = contentAdapter
        mBinding.recyclerViewAsses.addItemDecoration(
            SpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.spacing_1x), 0)
        )
        setUpSubmitAnswer()
    }

    private fun initCorrectRV(list: List<BaseCourseContent>) {
        correctAdapter = ExerciseContentAdapter(this.requireContext(),{}, {} ,{})
        val layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        mBinding.correctOutputLayout.outputLayout.layoutManager = layoutManager
        mBinding.correctOutputLayout.outputLayout.adapter = correctAdapter
        correctAdapter.submitList(getNewReferencedList(list))
    }

    private fun initIncorrectRV(list: List<BaseCourseContent>) {
        inCorrectAdapter = ExerciseContentAdapter(this.requireContext(),{},{}, {})
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mBinding.incorrectOutputLayout.incorrectRv.layoutManager = layoutManager
        mBinding.incorrectOutputLayout.incorrectRv.adapter = inCorrectAdapter
        inCorrectAdapter.submitList(getNewReferencedList(list))
    }

}
