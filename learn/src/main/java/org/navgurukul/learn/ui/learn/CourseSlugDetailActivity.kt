package org.navgurukul.learn.ui.learn

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import br.tiagohm.markdownview.css.styles.Github
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.navgurukul.learn.R
import org.navgurukul.learn.databinding.ActivityCourseSlugDetailBinding
import org.navgurukul.learn.ui.learn.adapter.CourseExerciseAdapter


class CourseSlugDetailActivity : AppCompatActivity() {

    companion object {
        private const val ARG_KEY_COURSE_ID = "arg_course_id"
        private const val ARG_KEY_SLUG_NAME = "arg_slug_name"
        private const val ARG_KEY_EXERCISE_NAME = "arg_exercise_name"

        fun start(context: Context, courseId: String, slugName: String, exerciseName: String) {
            val intent = Intent(context, CourseSlugDetailActivity::class.java)
            intent.putExtra(ARG_KEY_COURSE_ID, courseId)
            intent.putExtra(ARG_KEY_SLUG_NAME, slugName)
            intent.putExtra(ARG_KEY_EXERCISE_NAME, exerciseName)
            context.startActivity(intent)
        }
    }

    private lateinit var courseId: String
    private lateinit var slugName: String
    private lateinit var exerciseName: String
    private lateinit var mBinding: ActivityCourseSlugDetailBinding
    private var isPanelVisible = false
    private val viewModel: LearnViewModel by viewModel()
    private lateinit var mAdapter: CourseExerciseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_course_slug_detail)
        parseIntentData()
        mBinding.header.title = exerciseName
        fetchMarkDownContent()
        initUIElement()
        initRecyclerView()
    }


    private fun initUIElement() {
        mBinding.header.backButton.setOnClickListener {
            finish()
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
        mBinding.markDownContent.visibility = View.GONE
    }

    private fun hidePanel() {
        mBinding.slideComponent.slide.visibility = View.GONE
        mBinding.slideComponent.slide.closePane()
        mBinding.markDownContent.visibility = View.VISIBLE
    }


    private fun parseIntentData() {
        if (intent.hasExtra(ARG_KEY_COURSE_ID) && intent.hasExtra(ARG_KEY_SLUG_NAME)) {
            courseId = intent.getStringExtra(ARG_KEY_COURSE_ID)!!
            slugName = intent.getStringExtra(ARG_KEY_SLUG_NAME)!!
            exerciseName = intent.getStringExtra(ARG_KEY_EXERCISE_NAME)!!
        }
    }


    private fun fetchMarkDownContent() {
        viewModel.fetchExerciseSlug(courseId, slugName).observe(this, Observer {
            mBinding.progressBar.visibility = View.VISIBLE
            if (null != it && it.isNotEmpty() && null != it.first().content) {
                mBinding.progressBar.visibility = View.GONE
                mBinding.markDownContent.apply {
                    this.addStyleSheet(Github())
                    this.loadMarkdown(it.first().content)
                }
            }
        })
    }

    private fun initRecyclerView() {
        mAdapter = CourseExerciseAdapter {
            if (!it.first.slug.isNullOrBlank()) {
                start(this, courseId, it.first.slug!!, it.first.name)
                finish()
            }
        }
        val layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.slideComponent.recyclerviewCourseDetail.layoutManager = layoutManager
        mBinding.slideComponent.recyclerviewCourseDetail.adapter = mAdapter
        mAdapter.submitList(CourseDetailActivity.masterData)
    }


}