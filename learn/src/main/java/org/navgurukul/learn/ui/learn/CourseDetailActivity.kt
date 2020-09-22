package org.navgurukul.learn.ui.learn

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.CurrentStudy
import org.navgurukul.learn.courses.db.models.Exercise
import org.navgurukul.learn.databinding.ActivityCourseDetailBinding
import org.navgurukul.learn.ui.common.toolbarColor
import org.navgurukul.learn.ui.learn.adapter.CourseExerciseAdapter


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

        var masterData: MutableList<Exercise> = mutableListOf()
    }

    private lateinit var courseId: String
    private lateinit var courseName: String
    private lateinit var mBinding: ActivityCourseDetailBinding
    private lateinit var mAdapter: CourseExerciseAdapter
    private val viewModel: LearnViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_course_detail)
        parseIntentData()
        initToolBar()
        initExpandableToolBar()
        initRecyclerView()
        fetchData()
    }


    private fun parseIntentData() {
        if (intent.hasExtra(ARG_KEY_COURSE_ID) && intent.hasExtra(ARG_KEY_COURSE_NAME)) {
            courseId = intent.getStringExtra(ARG_KEY_COURSE_ID)!!
            courseName = intent.getStringExtra(ARG_KEY_COURSE_NAME)!!
        }
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
                CourseSlugDetailActivity.start(
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