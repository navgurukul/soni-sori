package org.navgurukul.learn.ui.learn

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.class_course_detail.*
import kotlinx.android.synthetic.main.fragment_class.*
import kotlinx.android.synthetic.main.item_revision_class.*
import kotlinx.android.synthetic.main.revision_selection_sheet.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.merakilearn.core.extentions.capitalizeWords
import org.merakilearn.core.extentions.fragmentArgs
import org.merakilearn.core.extentions.toBundle
import org.merakilearn.core.navigator.MerakiNavigator
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.*
import org.navgurukul.learn.databinding.FragmentClassBinding
import org.navgurukul.learn.databinding.FragmentExerciseBinding
import org.navgurukul.learn.ui.common.toast
import org.navgurukul.learn.ui.learn.adapter.RevisionClassAdapter
import org.navgurukul.learn.util.toDay
import org.navgurukul.learn.util.toTime

class ClassFragment  : Fragment() {

    private val args: CourseContentArgs by fragmentArgs()
    private val fragmentViewModel: ClassFragmentViewModel by viewModel(parameters = {
        parametersOf(args)
    })
    private lateinit var mBinding: FragmentClassBinding
    private val merakiNavigator: MerakiNavigator by inject()
    private lateinit var  mRevisionAdapter: RevisionClassAdapter

    companion object {
        fun newInstance(
            isFirst: Boolean,
            isLast: Boolean,
            isCompleted: Boolean,
            courseId: String,
            classId: String,
            courseContentType: CourseContentType
        ): ClassFragment {
            return ClassFragment().apply {
                arguments = CourseContentArgs(
                    isFirst,
                    isLast,
                    isCompleted,
                    courseId,
                    classId,
                    courseContentType
                ).toBundle()
            }
        }

        const val TAG = "ClassFragment"
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_class, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.revisionList.rootView.visibility = View.GONE
        mBinding.classDetail.rootView.visibility = View.GONE
        fragmentViewModel.viewEvents.observe(viewLifecycleOwner) {
            when (it) {
                is ClassFragmentViewModel.ClassFragmentViewEvents.ShowToast -> toast(it.toastText)

                is ClassFragmentViewModel.ClassFragmentViewEvents.ShowRevisionClasses -> {
                    initRevisionRecyclerView(it.revisionClasses)
                    mBinding.revisionList.rootView.visibility = View.VISIBLE
                }
                is ClassFragmentViewModel.ClassFragmentViewEvents.ShowClassData ->{
                    setUpClassData(it.courseClass)
                    mBinding.classDetail.rootView.visibility = View.VISIBLE
                }

                is ClassFragmentViewModel.ClassFragmentViewEvents.OpenLink -> startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.link)))
            }
        }

        fragmentViewModel.viewState.observe(viewLifecycleOwner, {
            mBinding.progressBar.visibility = if (it.isLoading) View.VISIBLE else View.GONE

        })
    }

    private fun setUpClassData(courseClass : CourseClassContent){
        tvSubTitle.text = courseClass.subTitle
        tvClassType.text = courseClass.type.name.capitalizeWords()
        tvClassLanguage.text = courseClass.displayableLanguage()
        tvDate.text = courseClass.timeDateRange()
        tvFacilatorName.text = courseClass.facilitator?.name

        tvBtnJoin.setOnClickListener {
            fragmentViewModel.handle(ClassFragmentViewModel.ClassFragmentViewActions.RequestJoinClass)
        }
    }

    private fun initRevisionRecyclerView(revisionClass: List<CourseClassContent>){
        mRevisionAdapter = RevisionClassAdapter {

        }
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recycler_view.layoutManager = layoutManager
        recycler_view.adapter = mRevisionAdapter
        mRevisionAdapter.submitList(revisionClass)
    }
}