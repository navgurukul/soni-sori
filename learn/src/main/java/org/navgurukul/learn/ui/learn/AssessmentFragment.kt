package org.navgurukul.learn.ui.learn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.batches_in_exercise.*
import org.merakilearn.core.extentions.toBundle
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.CourseClassContent
import org.navgurukul.learn.courses.db.models.CourseContentType
import org.navgurukul.learn.courses.network.model.Batch
import org.navgurukul.learn.courses.network.model.Classes
import org.navgurukul.learn.databinding.FragmentAssessmentBinding
import org.navgurukul.learn.ui.learn.adapter.BatchSelectionExerciseAdapter
import org.navgurukul.learn.ui.learn.adapter.OptionSelectionAdapter


class AssessmentFragment : Fragment() {

    private lateinit var mBinding: FragmentAssessmentBinding
    private lateinit var mClassAdapter: OptionSelectionAdapter


    companion object {
        fun newInstance(
            isFirst: Boolean,
            isLast: Boolean,
            isCompleted: Boolean,
            courseId: String,
            assessmentId : String,
            courseContentType: CourseContentType,
        ): ClassFragment {
            return ClassFragment().apply {
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


    }


    private fun initRecyclerviewOption(option: List<CourseClassContent>){
        mClassAdapter = OptionSelectionAdapter {

        }
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
        recyclerviewBatch.layoutManager = layoutManager
        recyclerviewBatch.adapter = mClassAdapter
        mClassAdapter.submitList(option.subList(0,4))
    }
}