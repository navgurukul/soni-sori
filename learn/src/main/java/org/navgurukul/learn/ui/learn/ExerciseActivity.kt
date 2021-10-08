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
import org.koin.core.parameter.parametersOf
import org.navgurukul.commonui.platform.ListSpacingDecoration
import org.navgurukul.learn.R
import org.navgurukul.learn.ui.common.toast


class ExerciseActivity : AppCompatActivity() {

    companion object {
        private const val ARG_KEY_COURSE_ID = "arg_course_id"
        private const val ARG_KEY_COURSE_NAME = "arg_course_name"
        private const val ARG_KEY_CURRENT_STUDY = "arg_course_name"

        fun start(context: Context, courseId: String, courseName: String) {
            val intent = Intent(context, ExerciseActivity::class.java)
            intent.putExtra(ARG_KEY_COURSE_ID, courseId)
            intent.putExtra(ARG_KEY_COURSE_NAME, courseName)
            context.startActivity(intent)
        }

        fun start(context: Context, courseStudy: CurrentStudy) {
            val intent = Intent(context, ExerciseActivity::class.java)
            intent.putExtra(ARG_KEY_CURRENT_STUDY, courseStudy)
            context.startActivity(intent)
        }
    }

    private lateinit var courseId: String
    private lateinit var courseName: String
    private lateinit var currentStudy: CurrentStudy

    private lateinit var mBinding: ActivityExerciseBinding
    private var isPanelVisible = false
    private val viewModel: ExerciseActivityViewModel by viewModel(
        parameters = { parametersOf(
            courseId
        )}
    )
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


        viewModel.viewEvents.observe(this, Observer {
            when (it) {
                is ExerciseActivityViewModel.ExerciseActivityViewEvents.ShowToast -> toast(it.toastText)
                is ExerciseActivityViewModel.ExerciseActivityViewEvents.ShowExerciseList -> updateExerciseListOnScreen(
                    it.exerciseList
                )
                is ExerciseActivityViewModel.ExerciseActivityViewEvents.SetCourseAndRenderUI -> setCurrentCourseDataAndRenderUI(
                    it.courseList
                )
            }
        })

        viewModel.viewState.observe(this, Observer {
            mBinding.progressBar.visibility = if (it.isLoading) View.VISIBLE else View.GONE
        })

    }

    private fun updateExerciseListOnScreen(courseList: List<Exercise>) {
        if (courseList.isNotEmpty()) {
            masterData = courseList as MutableList<Exercise>
            mAdapter.submitList(courseList)
        }
    }

    private fun setCurrentCourseDataAndRenderUI(courseList: List<Course>) {
        if (courseList.isNotEmpty()) {
            mBinding.progressBar.visibility = View.GONE
            courseId = courseList.firstOrNull()?.id.toString()
            courseName = courseList.firstOrNull()?.name.toString()
            renderUI()
        }
    }

    private fun parseIntentData() {
        if (intent.hasExtra(ARG_KEY_COURSE_ID) && intent.hasExtra(
                ARG_KEY_COURSE_NAME
            )) {
            courseId = intent.getStringExtra(ARG_KEY_COURSE_ID)!!
            courseName = intent.getStringExtra(ARG_KEY_COURSE_NAME)!!
            renderUI()
        } else if(intent.hasExtra(ARG_KEY_CURRENT_STUDY)){
            currentStudy = intent.getParcelableExtra(ARG_KEY_CURRENT_STUDY)!!

            currentStudy.courseName?.let{
                courseName = it
            }
            courseId = currentStudy.courseId

            renderUI()
            launchExerciseFragment(currentStudy)
        } else {
            val action: String? = intent?.action
            val data: Uri? = intent?.data
            val uriString = data.toString()
            if (action == Intent.ACTION_VIEW) {
                if (uriString.contains("/course/")) {
                    fetchClassDataAndShow(uriString.split("/").last())
                }
            }
        }
    }

    private fun fetchClassDataAndShow(last: String) {
        courseId = last
        viewModel.handle(ExerciseActivityViewModel.ExerciseActivityViewActions.FetchCourseExerciseDataWithCourse(courseId))
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
//        fetchData()
    }

    private fun fetchData() {
        viewModel.handle(ExerciseActivityViewModel.ExerciseActivityViewActions.FetchCourseExercises(courseId))
    }

    private fun initRecyclerViewExerciseList() {
        mAdapter = CourseExerciseAdapter {
            if (!it.first.slug.isNullOrBlank()) {
                launchExerciseFragment(
                    CurrentStudy(
                        courseId = courseId,
                        courseName = courseName,
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
        mBinding.recyclerviewCourseExerciseList.addItemDecoration(
            ListSpacingDecoration(
                mBinding.recyclerviewCourseExerciseList.context,
                R.dimen.dimen_0_dp,
            R.dimen.dimen_course_content_margin
        )
        )
    }

    private fun launchExerciseFragment(currentStudy: CurrentStudy) {
        val manager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = manager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.slide_in_to_left,
            R.anim.slide_out_from_right,
            R.anim.slide_in_to_left,
            R.anim.slide_out_from_right
        )
        transaction.replace(org.navgurukul.learn.R.id.exerciseContentContainer,
            ExerciseFragment.newInstance(currentStudy), ExerciseFragment.TAG)
        transaction.commit()
    }

}