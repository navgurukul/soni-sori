package org.navgurukul.learn.ui.learn

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.CurrentStudy
import org.navgurukul.learn.courses.db.models.Exercise
import org.navgurukul.learn.databinding.ActivityCourseSlugDetailBinding
import org.navgurukul.learn.ui.learn.adapter.CourseExerciseAdapter
import org.navgurukul.learn.ui.learn.adapter.ExerciseSlugAdapter


class CourseSlugDetailActivity : AppCompatActivity() {

    companion object {
        private const val ARG_KEY_CURRENT_STUDY = "arg_current_study"
        fun start(
            context: Context,
            currentStudy: CurrentStudy
        ) {
            val intent = Intent(context, CourseSlugDetailActivity::class.java)
            intent.putExtra(ARG_KEY_CURRENT_STUDY, currentStudy)
            context.startActivity(intent)
        }
    }

    private lateinit var currentStudy: CurrentStudy
    private lateinit var mBinding: ActivityCourseSlugDetailBinding
    private var isPanelVisible = false
    private val viewModel: LearnViewModel by viewModel()
    private lateinit var mAdapter: CourseExerciseAdapter
    private lateinit var slugAdapter: ExerciseSlugAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_course_slug_detail)
        parseIntentData()
        mBinding.header.title = currentStudy.exerciseName
        initUIElement()
        initRecyclerViewSlidingPanel()
        initContentRV()
        saveCourseExercise()
        initSwipeRefresh()
    }


    private fun initSwipeRefresh() {
        mBinding.swipeContainer.setOnRefreshListener {
            fetchSlugContent(currentStudy.exerciseId, true)
            mBinding.swipeContainer.isRefreshing = false
        }
    }

    private fun initUIElement() {
        mBinding.header.backButton.setOnClickListener {
            moveToPreviousPage()
        }
        mBinding.header.ivOption.setOnClickListener {
            initOptionClickListener()
        }
    }


    private fun initOptionClickListener() {
        if (isPanelVisible) {
            isPanelVisible = false
            hidePanel()
        } else {
            isPanelVisible = true
            showPanel()
        }
    }

    private fun showPanel() {
        mBinding.slideComponent.slide.visibility = View.VISIBLE
        mBinding.slideComponent.slide.openPane()
        mBinding.recyclerViewSlug.visibility = View.GONE
    }

    private fun hidePanel() {
        mBinding.slideComponent.slide.visibility = View.GONE
        mBinding.slideComponent.slide.closePane()
        mBinding.recyclerViewSlug.visibility = View.VISIBLE
    }


    private fun parseIntentData() {
        if (intent.hasExtra(ARG_KEY_CURRENT_STUDY)) {
            currentStudy = intent.getSerializableExtra(ARG_KEY_CURRENT_STUDY) as CurrentStudy
        }
    }


    private fun initContentRV() {
        slugAdapter = ExerciseSlugAdapter {

        }
        val layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.recyclerViewSlug.layoutManager = layoutManager
        mBinding.recyclerViewSlug.adapter = slugAdapter
        fetchSlugContent(currentStudy.exerciseId, false)

    }

    private fun fetchSlugContent(exerciseId: String, forceUpdate: Boolean) {
        mBinding.progressBar.visibility = View.VISIBLE
        viewModel.fetchExerciseSlug(exerciseId, currentStudy.courseId, forceUpdate)
            .observe(this, Observer {
                mBinding.progressBar.visibility = View.GONE
                if (null != it && it.isNotEmpty()) {
                    val data = parseDataForContent(it)
                    slugAdapter.submitList(data)
                }
            })
    }

    private fun parseDataForContent(it: List<Exercise>?): List<Exercise.ExerciseSlugDetail> {
        return it?.firstOrNull()?.content ?: return mutableListOf()
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
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.slideComponent.recyclerviewCourseDetail.layoutManager = layoutManager
        mBinding.slideComponent.recyclerviewCourseDetail.adapter = mAdapter
        if (CourseDetailActivity.masterData.isEmpty()) {
            fetchAndSetMasterData()
        } else
            mAdapter.submitList(CourseDetailActivity.masterData)
    }

    private fun fetchAndSetMasterData() {
        viewModel.fetchCourseExerciseData(currentStudy.courseId).observe(this, Observer {
            mBinding.progressBar.visibility = View.VISIBLE
            if (null != it && it.isNotEmpty()) {
                mBinding.progressBar.visibility = View.GONE
                CourseDetailActivity.masterData = it as MutableList<Exercise>
                mAdapter.submitList(CourseDetailActivity.masterData)
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
        CourseDetailActivity.start(this, currentStudy.courseId, currentStudy.courseName)
        finish()
    }

}