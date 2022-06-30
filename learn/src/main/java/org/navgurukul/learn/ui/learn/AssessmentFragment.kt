package org.navgurukul.learn.ui.learn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_assessment.*
import kotlinx.android.synthetic.main.incorrect_output_layout.*
import kotlinx.android.synthetic.main.incorrect_output_layout.view.*
import kotlinx.android.synthetic.main.item_output_content.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.merakilearn.core.extentions.fragmentArgs
import org.merakilearn.core.extentions.toBundle
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.*
import org.navgurukul.learn.databinding.FragmentAssessmentBinding
import org.navgurukul.learn.ui.common.toast
import org.navgurukul.learn.ui.learn.adapter.*
import org.navgurukul.learn.ui.learn.viewholder.AssessmentFragmentViewModel


class AssessmentFragment : Fragment() {

    private val args: CourseContentArgs by fragmentArgs()
    private lateinit var mBinding: FragmentAssessmentBinding
    private lateinit var contentAdapter: ExerciseContentAdapter
    private lateinit var correctAdapter: ExerciseContentAdapter
    private lateinit var inCorrectAdapter : ExerciseContentAdapter
    private var selectedOption : OptionResponse? = null
    private val fragmentViewModel: AssessmentFragmentViewModel by viewModel(parameters = {
        parametersOf(args)
    })


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

        fragmentViewModel.viewEvents.observe(viewLifecycleOwner) {
            when (it) {
                is AssessmentFragmentViewModel.AssessmentFragmentViewEvents.ShowToast -> toast(it.toastText)
                is AssessmentFragmentViewModel.AssessmentFragmentViewEvents.ShowCorrectOutput -> {
                    initCorrectRV(it.list)
                    mBinding.correctOutputLayout.root.visibility = View.VISIBLE

                }
                is AssessmentFragmentViewModel.AssessmentFragmentViewEvents.ShowIncorrectOutput->{
                    mBinding.incorrectOutputLayout.visibility = View.VISIBLE
                    mBinding.incorrectOutputLayout.incorrectRv.isVisible = true
                    incorrectOutputHandling(it.list)
                }
            }
        }
        fragmentViewModel.viewState.observe(viewLifecycleOwner) {
            mBinding.progressBar.visibility = if (it.isLoading) View.VISIBLE else View.GONE


            if (!it.isError)
                contentAdapter.submitList(it.assessmentContentList)




        }


        initContentRv()
//        initIncorrectRV()
//        initRecyclerviewOption()
    }

    private fun setUpSubmitAnswer(){
        mBinding.btnSubmit.setOnClickListener{
            mBinding.btnSubmit.visibility = View.GONE
            selectedOption.let {
                it?.let { it1 ->
                    AssessmentFragmentViewModel.AssessmentFragmentViewActions.OptionSelectedClicked(it1)
                }?.let { it2 -> fragmentViewModel.handle(it2) }
            }
        }
    }

    private fun incorrectOutputHandling(list: List<BaseCourseContent>) {
        btnSeeExplanation.setOnClickListener {
            initIncorrectRV(list)
            incorrectRv.isVisible = true
        }

        btnRetry.setOnClickListener {
            contentAdapter.submitList(resetList())
            mBinding.incorrectOutputLayout.isVisible = false
        }
    }

    private fun resetList(): MutableList<BaseCourseContent>? {
        val newList = fragmentViewModel.viewState.value?.assessmentContentList?.toMutableList()
        newList?.forEach {
            if(it.component == BaseCourseContent.COMPONENT_OPTIONS){
                    val item = it as OptionsBaseCourseContent
                    item.value = item.value.toMutableList().map{ it.copy(isSelected = false) }
            }
        }
        return newList
    }

    private fun initContentRv(){
        contentAdapter = ExerciseContentAdapter(this.requireContext(),{

        }, {

        } ,{
            AssessmentFragmentViewModel.AssessmentFragmentViewActions.OptionSelectedClicked(it)
            mBinding.btnSubmit.visibility = View.VISIBLE
        })
        val layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        recycler_view_asses.layoutManager = layoutManager
        recycler_view_asses.adapter = contentAdapter
        setUpSubmitAnswer()


    }

    private fun initCorrectRV(list: List<BaseCourseContent>){
        correctAdapter = ExerciseContentAdapter(this.requireContext(),{},{})
        val layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            correct_output_layout.outputLayout.layoutManager = layoutManager
        correct_output_layout.outputLayout.adapter = correctAdapter
        correctAdapter.submitList(list)

    }

    private fun initIncorrectRV(list: List<BaseCourseContent>) {
        inCorrectAdapter = ExerciseContentAdapter(this.requireContext(),{},{},
            {
                AssessmentFragmentViewModel.AssessmentFragmentViewActions.OptionSelectedClicked(it)
        })
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mBinding.incorrectOutputLayout.incorrectRv.layoutManager = layoutManager
        mBinding.incorrectOutputLayout.incorrectRv.adapter = inCorrectAdapter

        inCorrectAdapter.submitList(list)
    }


}
