package org.navgurukul.learn.ui.learn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_assessment.*
import kotlinx.android.synthetic.main.incorrect_output_layout.*
import kotlinx.android.synthetic.main.item_output_content.*
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
    private val fragmentViewModel: AssessmentFragmentViewModel by viewModel(parameters = {
        parametersOf(args)
    })

//    val assessmentContentList: List<BaseCourseContent> = listOf()


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

        fragmentViewModel.viewEvents.observe(viewLifecycleOwner) {
            when (it) {
                is AssessmentFragmentViewModel.AssessmentFragmentViewEvents.ShowToast -> toast(it.toastText)
                is AssessmentFragmentViewModel.AssessmentFragmentViewEvents.ShowCorrectOutput -> {


                }
                is AssessmentFragmentViewModel.AssessmentFragmentViewEvents.ShowIncorrectOutput->{
                    mBinding.incorrectOutputLayout.visibility = View.VISIBLE
                    incorrectOutputHandling()
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

    private fun incorrectOutputHandling(){
        btnSeeExplanation.setOnClickListener {
//            initIncorrectRV()
        }

        btnRetry.setOnClickListener {

        }
    }

    private fun initContentRv(){
        contentAdapter = ExerciseContentAdapter(this.requireContext(),{

        }, {

        }) {

        }
        val layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        recycler_view_asses.layoutManager = layoutManager
        recycler_view_asses.adapter = contentAdapter


    }

    private fun initCorrectRV(list: List<BaseCourseContent>){
        correctAdapter = ExerciseContentAdapter(requireContext(), {
        },{

        }, {

        })
    }

    private fun initIncorrectRV(){
        inCorrectAdapter = ExerciseContentAdapter(this.requireContext(),{},{},
            {

        })
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        outputLayout.layoutManager = layoutManager
        outputLayout.adapter = inCorrectAdapter
    }


}