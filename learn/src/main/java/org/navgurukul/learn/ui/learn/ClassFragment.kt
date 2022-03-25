package org.navgurukul.learn.ui.learn

import android.graphics.Paint
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import kotlinx.android.parcel.Parcelize
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.merakilearn.core.extentions.fragmentArgs
import org.merakilearn.core.extentions.toBundle
import org.merakilearn.core.navigator.MerakiNavigator
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.CourseContentType
import org.navgurukul.learn.databinding.FragmentExerciseBinding
import org.navgurukul.learn.ui.common.toast
import org.navgurukul.learn.ui.learn.adapter.ExerciseContentAdapter

@Parcelize
data class ClassFragmentArgs(
    val isFirst: Boolean,
    val isLast: Boolean,
    var isCompleted: Boolean,
    val courseId: String,
    val classId: String,
    val courseContentType: CourseContentType,
) : Parcelable

class ClassFragment  : Fragment() {

    private val args: ExerciseFragmentArgs by fragmentArgs()
    private val fragmentViewModel: ExerciseFragmentViewModel by viewModel(parameters = {
        parametersOf(args)
    })
    private lateinit var navigationClickListener: ExerciseFragment.ExerciseNavigationClickListener
    private lateinit var mBinding: FragmentExerciseBinding
    private val merakiNavigator: MerakiNavigator by inject()

    companion object {
        fun newInstance(
            isFirst: Boolean,
            isLast: Boolean,
            isCompleted: Boolean,
            courseId: String,
            exerciseId: String,
            courseContentType: CourseContentType
        ): ExerciseFragment {
            return ExerciseFragment().apply {
                arguments = ExerciseFragmentArgs(
                    isFirst,
                    isLast,
                    isCompleted,
                    courseId,
                    exerciseId,
                    courseContentType
                ).toBundle()
            }
        }

        const val TAG = "ExerciseFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_exercise, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentViewModel.viewEvents.observe(viewLifecycleOwner, {
            when (it) {
                is ExerciseFragmentViewModel.ExerciseFragmentViewEvents.ShowToast -> toast(it.toastText)
            }
        })

        fragmentViewModel.viewState.observe(viewLifecycleOwner, {
            mBinding.progressBar.visibility = if (it.isLoading) View.VISIBLE else View.GONE
//            showErrorScreen(it.isError)

        })

//        setIsCompletedView(args.isCompleted)

//        initScreenRefresh()

        mBinding.btnMarkCompleted.setPaintFlags(mBinding.btnMarkCompleted.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
        mBinding.btnMarkCompleted.setOnClickListener {
//            markCompletedClicked()
        }
    }

}