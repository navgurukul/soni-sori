package org.navgurukul.learn.ui.learn

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.parcel.Parcelize
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.merakilearn.core.dynamic.module.DynamicFeatureModuleManager
import org.merakilearn.core.extentions.fragmentArgs
import org.merakilearn.core.navigator.MerakiNavigator
import org.navgurukul.commonui.platform.BaseFragment
import org.navgurukul.commonui.platform.SpaceItemDecoration
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.CodeBaseCourseContent
import org.navgurukul.learn.courses.db.models.CurrentStudy
import org.navgurukul.learn.courses.db.models.Exercise
import org.navgurukul.learn.courses.db.models.LinkBaseCourseContent
import org.navgurukul.learn.databinding.FragmentExerciseBinding
import org.navgurukul.learn.ui.learn.adapter.CourseExerciseAdapter
import org.navgurukul.learn.ui.learn.adapter.ExerciseContentAdapter

class ExerciseFragment: Fragment() {

    private val viewModel: ExerciseViewModel by viewModel()
    private lateinit var currentStudy: CurrentStudy
    private lateinit var mBinding: FragmentExerciseBinding
    private var isPanelVisible = false
    private lateinit var mAdapter: CourseExerciseAdapter
    private lateinit var contentAdapter: ExerciseContentAdapter
    private var masterData: MutableList<Exercise> = mutableListOf()
    private val merakiNavigator: MerakiNavigator by inject()

    @Parcelize
    data class CurrentStudyArgs(
        val currentStudy: CurrentStudy
    ) : Parcelable

    private val currentCourseArgs: CurrentStudyArgs by fragmentArgs()

    companion object {
        private const val ARG_KEY_COURSE_ID = "arg_course_id"
        private const val ARG_KEY_COURSE_NAME = "arg_course_name"

        fun start(context: Context, courseId: String, courseName: String) {
            val intent = Intent(context, CourseDetailActivity::class.java)
            intent.putExtra(ARG_KEY_COURSE_ID, courseId)
            intent.putExtra(ARG_KEY_COURSE_NAME, courseName)
            context.startActivity(intent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_exercise, container, false)
        return mBinding.root

    }

    private fun parseIntentData() {
        currentStudy = currentCourseArgs.currentStudy
        renderUI()
       /*
        if (inten.hasExtra(CourseSlugDetailActivity.ARG_KEY_CURRENT_STUDY)) {
            currentStudy = intent.getSerializableExtra(CourseSlugDetailActivity.ARG_KEY_CURRENT_STUDY) as CurrentStudy
            renderUI()
        } else {
            val action: String? = intent?.action
            val data: Uri? = intent?.data
            val uriString = data.toString()
            if (action == Intent.ACTION_VIEW) {
                if (uriString.contains("/exercise/")) {
                    val courseId = data?.pathSegments?.get(1)
                    val exerciseId = uriString.split("/").last()
                    fetchExerciseDataAndShow(exerciseId, courseId.toString())
                }
            } else {
                renderUI()
            }
        }
        */
    }

    private fun renderUI() {
        initContentRV()
        saveCourseExercise()
        initSwipeRefresh()
    }


    private fun initSwipeRefresh() {
        mBinding.swipeContainer.setOnRefreshListener {
            fetchExerciseContent(currentStudy.exerciseId, true)
            mBinding.swipeContainer.isRefreshing = false
        }
    }

    private fun initContentRV() {
        contentAdapter = ExerciseContentAdapter(this.requireContext(), {
            if (it is CodeBaseCourseContent) {
                if (!it.value.isNullOrBlank()) {
                    merakiNavigator.openPlayground(this.requireContext(), it.value)
                }
            } else if (it is LinkBaseCourseContent) {
                it.link?.let { url ->
                    merakiNavigator.openCustomTab(url, this.requireContext())
                }
            }
        }) {
            it?.let { action ->
                action.url?.let { url ->
                    merakiNavigator.openDeepLink(this.requireActivity(), url, action.data)
                }
            }

        }


        val layoutManager =
            LinearLayoutManager(this.requireContext(), LinearLayoutManager.VERTICAL, false)
        mBinding.recyclerViewSlug.layoutManager = layoutManager
        mBinding.recyclerViewSlug.adapter = contentAdapter
        mBinding.recyclerViewSlug.addItemDecoration(
            SpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.spacing_4x), 0)
        )
        fetchExerciseContent(currentStudy.exerciseId, false)

    }

    private fun fetchExerciseContent(exerciseId: String, forceUpdate: Boolean) {
//        mBinding.progressBar.visibility = View.VISIBLE
//        viewModel.fetchExerciseSlug(exerciseId, currentStudy.courseId, forceUpdate)
//            .observe(this, {
//                mBinding.progressBar.visibility = View.GONE
//                if (null != it && it.isNotEmpty()) {
//                    val data = parseDataForContent(it)
//                    contentAdapter.submitList(data)
//                }
//            })
    }

    private fun saveCourseExercise() {
//        viewModel.saveCourseExerciseCurrent(currentStudy)
    }


}