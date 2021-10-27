package org.navgurukul.learn.ui.learn

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.merakilearn.core.navigator.MerakiNavigator
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.CurrentStudy
import org.navgurukul.learn.courses.db.models.Exercise
import org.navgurukul.learn.databinding.ActivityCourseDetailBinding
import org.navgurukul.learn.ui.common.toolbarColor
import org.navgurukul.learn.ui.learn.adapter.CourseExerciseAdapter
import org.navgurukul.learn.util.LearnUtils


class CourseDetailActivity : AppCompatActivity() {

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

    private var masterData: MutableList<Exercise> = mutableListOf()
    private lateinit var courseId: String
    private lateinit var courseName: String
    private lateinit var mBinding: ActivityCourseDetailBinding
    private lateinit var mAdapter: CourseExerciseAdapter
    private val viewModel: LearnViewModel by viewModel()
    private val merakiNavigator: MerakiNavigator by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_course_detail)
        if (LearnUtils.isUserLoggedIn(this)) {
            parseIntentData()
        } else
            merakiNavigator.restartApp(this, true)

    }

    private fun renderUI() {
        initToolBar()
        initExpandableToolBar()
        initRecyclerView()
        fetchData()
    }


    private fun parseIntentData() {
        if (intent.hasExtra(ARG_KEY_COURSE_ID) && intent.hasExtra(ARG_KEY_COURSE_NAME)) {
            courseId = intent.getStringExtra(ARG_KEY_COURSE_ID)!!
            courseName = intent.getStringExtra(ARG_KEY_COURSE_NAME)!!
            renderUI()
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
        mBinding.contentCourseDetail.progressBar.visibility = View.VISIBLE
        viewModel.fetchCourseExerciseDataWithCourse(courseId).observe(this, Observer {
            if (null != it && it.isNotEmpty()) {
                mBinding.contentCourseDetail.progressBar.visibility = View.GONE
                courseId = it.firstOrNull()?.id.toString()
                courseName = it.firstOrNull()?.name.toString()
                renderUI()
            }
        })
    }

    private fun initToolBar() {
        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    private fun initExpandableToolBar() {
        mBinding.toolbarLayout.setExpandedTitleColor(toolbarColor())
        mBinding.toolbarLayout.setCollapsedTitleTextColor(toolbarColor())
        mBinding.toolbarLayout.title = courseName
    }


    private fun initRecyclerView() {
        mAdapter = CourseExerciseAdapter {
            if (!it.first.slug.isNullOrBlank())
                ExerciseActivity.start(
                    this,
                    CurrentStudy(
                        courseId, courseName, it.first.slug!!, it.first.name, it.first.id
                    )
                )
            finish()
        }
        val layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.contentCourseDetail.recyclerviewCourseDetail.layoutManager = layoutManager
        mBinding.contentCourseDetail.recyclerviewCourseDetail.adapter = mAdapter
    }


    private fun fetchData() {
        mBinding.contentCourseDetail.progressBar.visibility = View.VISIBLE
        viewModel.fetchCourseExerciseData(courseId).observe(this, Observer {
            if (null != it && it.isNotEmpty()) {
                mBinding.contentCourseDetail.progressBar.visibility = View.GONE
                masterData = it as MutableList<Exercise>
                mAdapter.submitList(it)
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        masterData.clear()
    }
}