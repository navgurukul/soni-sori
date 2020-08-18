package org.navgurukul.learn.ui.learn

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.appbar.AppBarLayout
import org.navgurukul.learn.R
import org.navgurukul.learn.databinding.ActivityCourseDetailBinding
import org.navgurukul.learn.ui.common.toolbarColor

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

    private lateinit var courseId: String
    private lateinit var courseName: String
    private lateinit var mBinding: ActivityCourseDetailBinding
    private var isShow = true
    private var scrollRange = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_course_detail)
        parseIntentData()
        initToolBar()
        initExpandableToolBar()
    }

    private fun parseIntentData() {
        if (intent.hasExtra(ARG_KEY_COURSE_ID) && intent.hasExtra(ARG_KEY_COURSE_NAME)) {
            courseId = intent.getStringExtra(ARG_KEY_COURSE_ID)!!
            courseName = intent.getStringExtra(ARG_KEY_COURSE_NAME)!!
            mBinding.courseDetailHeader.title = courseName
        }
    }

    private fun initToolBar() {
        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayShowCustomEnabled(true)
    }

    private fun initExpandableToolBar() {
        mBinding.toolbarLayout.setExpandedTitleColor(toolbarColor())
        mBinding.toolbarLayout.setCollapsedTitleTextColor(toolbarColor())
        mBinding.toolbarLayout.title = courseName

      /*  mBinding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout: AppBarLayout?, verticalOffset: Int ->
            if (scrollRange == -1)
                scrollRange = appBarLayout?.totalScrollRange!!
            if (scrollRange + verticalOffset == 0) {
                mBinding.toolbarLayout.title = courseName
                isShow = true
            } else if (isShow) {
                mBinding.toolbarLayout.title = " "
                isShow = false
            }
        })*/
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }
}