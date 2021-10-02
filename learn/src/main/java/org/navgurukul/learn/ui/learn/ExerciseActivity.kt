package org.navgurukul.learn.ui.learn

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.merakilearn.core.dynamic.module.DynamicFeatureModuleManager
import org.merakilearn.core.navigator.MerakiNavigator
import org.navgurukul.commonui.platform.SpaceItemDecoration
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.*
import org.navgurukul.learn.databinding.ActivityExerciseBinding
import org.navgurukul.learn.ui.learn.adapter.CourseExerciseAdapter
import org.navgurukul.learn.ui.learn.adapter.ExerciseContentAdapter
import org.navgurukul.learn.util.LearnUtils


class ExerciseActivity : AppCompatActivity() {

    companion object {
        private const val ARG_KEY_CURRENT_STUDY = "arg_current_study"
        fun start(
            context: Context,
            currentStudy: CurrentStudy
        ) {
            val intent = Intent(context, ExerciseActivity::class.java)
            intent.putExtra(ARG_KEY_CURRENT_STUDY, currentStudy)
            context.startActivity(intent)
        }
    }

    private lateinit var currentStudy: CurrentStudy
    private lateinit var mBinding: ActivityExerciseBinding
    private var isPanelVisible = false
    private val viewModel: LearnViewModel by viewModel()
    private lateinit var mAdapter: CourseExerciseAdapter
    private lateinit var contentAdapter: ExerciseContentAdapter
    private var masterData: MutableList<Exercise> = mutableListOf()
    private val merakiNavigator: MerakiNavigator by inject()
    private val dynamicFeatureModuleManager: DynamicFeatureModuleManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_exercise)
        // Instantiate an instance of SplitInstallManager for the dynamic feature module
        if (LearnUtils.isUserLoggedIn(this)) {
            parseIntentData()
        } else
            merakiNavigator.restartApp(this, true)

    }

    private fun parseIntentData() {
        if (intent.hasExtra(ARG_KEY_CURRENT_STUDY)) {
            currentStudy = intent.getSerializableExtra(ARG_KEY_CURRENT_STUDY) as CurrentStudy
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
    }

    private fun fetchExerciseDataAndShow(exerciseId: String, courseId: String) {
        viewModel.fetchExerciseSlug(exerciseId, courseId, true)
            .observe(this, Observer {
                if (null != it && it.isNotEmpty()) {
                    currentStudy = CurrentStudy(
                        courseId = courseId,
                        courseName = it.firstOrNull()?.courseName,
                        exerciseSlugName = it.firstOrNull()?.slug!!,
                        exerciseName = it.firstOrNull()?.name,
                        exerciseId = exerciseId
                    )
                    renderUI()
                }
            })
    }

    private fun initUIElement() {
        mBinding.header.backButton.setOnClickListener {
            moveToPreviousPage()
        }
    }


    private fun renderUI() {
        mBinding.header.title = currentStudy.courseName
        initUIElement()
        initRecyclerViewSlidingPanel()
        saveCourseExercise()
    }

    private fun parseDataForContent(it: List<Exercise>?): List<BaseCourseContent> {
        return it?.firstOrNull()?.content ?: return listOf()
    }

    private fun initRecyclerViewSlidingPanel() {
        mAdapter = CourseExerciseAdapter {
            if (!it.first.slug.isNullOrBlank()) {
                start(
                    this,
                    CurrentStudy(
                        courseId = currentStudy.courseId,
                        courseName = currentStudy.courseName,
                        exerciseSlugName = it.first.slug!!,
                        exerciseName = it.first.name,
                        exerciseId = it.first.id
                    )
                )
                finish()
            }
        }
        val layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mBinding.recyclerviewCourseDetail.layoutManager = layoutManager
        mBinding.recyclerviewCourseDetail.adapter = mAdapter
        fetchAndSetMasterData()
    }

    private fun fetchAndSetMasterData() {
        viewModel.fetchCourseExerciseData(currentStudy.courseId).observe(this, Observer {
            if (null != it && it.isNotEmpty()) {
                masterData = it as MutableList<Exercise>
                mAdapter.submitList(masterData)
            }
        })
    }

    private fun saveCourseExercise() {
        viewModel.saveCourseExerciseCurrent(currentStudy)
    }


    override fun onBackPressed() {
        moveToPreviousPage()
    }

    private fun moveToPreviousPage() {
        CourseDetailActivity.start(this, currentStudy.courseId, currentStudy.courseName ?: "")
        finish()
    }

}