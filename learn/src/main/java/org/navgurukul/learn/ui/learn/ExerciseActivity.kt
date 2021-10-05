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
import org.navgurukul.learn.courses.db.models.*
import org.navgurukul.learn.databinding.ActivityExerciseBinding
import org.navgurukul.learn.ui.learn.adapter.CourseExerciseAdapter
import org.navgurukul.learn.ui.learn.adapter.ExerciseContentAdapter
import org.navgurukul.learn.util.LearnUtils
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction


class ExerciseActivity : AppCompatActivity() {

    companion object {
        private const val ARG_KEY_COURSE_ID = "arg_course_id"
        private const val ARG_KEY_COURSE_NAME = "arg_course_name"
        private const val ARG_KEY_CURRENT_STUDY = "arg_course_name"

        fun start(context: Context, courseId: String, courseName: String) {
            val intent = Intent(context, CourseDetailActivity::class.java)
            intent.putExtra(ARG_KEY_COURSE_ID, courseId)
            intent.putExtra(ARG_KEY_COURSE_NAME, courseName)
            context.startActivity(intent)
        }

        fun start(context: Context, courseStudy: CurrentStudy) {
            val intent = Intent(context, CourseDetailActivity::class.java)
            intent.putExtra(ARG_KEY_CURRENT_STUDY, courseStudy)
            context.startActivity(intent)
        }
    }

    private lateinit var courseId: String
    private lateinit var courseName: String
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
        mBinding = DataBindingUtil.setContentView(this, org.navgurukul.learn.R.layout.activity_exercise)
        // Instantiate an instance of SplitInstallManager for the dynamic feature module
        if (LearnUtils.isUserLoggedIn(this)) {
            parseIntentData()
        } else
            merakiNavigator.restartApp(this, true)

    }

    private fun parseIntentData() {
        if (intent.hasExtra(ARG_KEY_COURSE_ID) && intent.hasExtra(
                ARG_KEY_COURSE_NAME
            )) {
            courseId = intent.getStringExtra(ARG_KEY_COURSE_ID)!!
            courseName = intent.getStringExtra(ARG_KEY_COURSE_NAME)!!
            renderUI()
        }
        else if(intent.hasExtra(ARG_KEY_CURRENT_STUDY)){
            currentStudy = intent.getParcelableExtra(ARG_KEY_CURRENT_STUDY)!!
            renderUI()
            launchExerciseFragment(currentStudy)
        }
//        else {
//            val action: String? = intent?.action
//            val data: Uri? = intent?.data
//            val uriString = data.toString()
//            if (action == Intent.ACTION_VIEW) {
//                if (uriString.contains("/exercise/")) {
//                    val courseId = data?.pathSegments?.get(1)
//                    val exerciseId = uriString.split("/").last()
//                    fetchExerciseDataAndShow(exerciseId, courseId.toString())
//                }
//            } else {
//                renderUI()
//            }
//        }
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
            finish()
        }
    }


    private fun renderUI() {
        mBinding.header.title = courseName
        initUIElement()
        initRecyclerViewExerciseList()
        fetchData()
    }

    private fun fetchData() {
        mBinding.progressBar.visibility = View.VISIBLE
        viewModel.fetchCourseExerciseData(courseId).observe(this, Observer {
            if (null != it && it.isNotEmpty()) {
                mBinding.progressBar.visibility = View.GONE
                masterData = it as MutableList<Exercise>
                mAdapter.submitList(it)
            }
        })
    }

    private fun parseDataForContent(it: List<Exercise>?): List<BaseCourseContent> {
        return it?.firstOrNull()?.content ?: return listOf()
    }

    private fun initRecyclerViewExerciseList() {
        mAdapter = CourseExerciseAdapter {
            if (!it.first.slug.isNullOrBlank()) {
                launchExerciseFragment(
                    CurrentStudy(
                        courseId = currentStudy.courseId,
                        courseName = currentStudy.courseName,
                        exerciseSlugName = it.first.slug!!,
                        exerciseName = it.first.name,
                        exerciseId = it.first.id
                    )
                )
            }
        }
        val layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mBinding.recyclerviewCourseExerciseList.layoutManager = layoutManager
        mBinding.recyclerviewCourseExerciseList.adapter = mAdapter
        fetchAndSetMasterData()
    }

    private fun launchExerciseFragment(currentStudy: CurrentStudy) {
        val manager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = manager.beginTransaction()
        transaction.replace(org.navgurukul.learn.R.id.exerciseContentContainer,
            ExerciseFragment.newInstance(currentStudy), ExerciseFragment.TAG)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun fetchAndSetMasterData() {
        viewModel.fetchCourseExerciseData(currentStudy.courseId).observe(this, Observer {
            if (null != it && it.isNotEmpty()) {
                masterData = it as MutableList<Exercise>
                mAdapter.submitList(masterData)
            }
        })
    }

}