package org.navgurukul.learn.ui.learn.c4ca

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.parcel.Parcelize
import org.koin.androidx.viewmodel.compat.SharedViewModelCompat.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.merakilearn.core.extentions.KEY_ARG
import org.merakilearn.core.extentions.toBundle
import org.navgurukul.commonui.platform.ListSpacingDecoration
import org.navgurukul.learn.R
import org.navgurukul.learn.databinding.ActivityExerciseBinding
import org.navgurukul.learn.databinding.ActivityModuleCourseContentBinding
import org.navgurukul.learn.ui.learn.CourseContentActivity
import org.navgurukul.learn.ui.learn.CourseContentActivityArgs
import org.navgurukul.learn.ui.learn.CourseContentActivityViewActions
import org.navgurukul.learn.ui.learn.CourseContentActivityViewModel
import org.navgurukul.learn.ui.learn.adapter.CourseContentAdapter

@Parcelize
data class ModuleCourseContentActivityArgs(val courseId: Int, val contentId: String?= null) : Parcelable

class ModuleCourseContentActivity : AppCompatActivity() {
    private lateinit var mBinding : ActivityModuleCourseContentBinding


    companion object {
        fun start(context: Context, courseId: Int, contentId: String? = null) {
            val intent = Intent(context, ModuleCourseContentActivity::class.java).apply {
                putExtras(ModuleCourseContentActivityArgs(courseId,  contentId).toBundle()!!)
            }
            context.startActivity(intent)
        }

        const val MODULE_URL_INDEX = 0
        const val PATHWAY_URL_INDEX = 1
        const val COURSE_URL_INDEX = 3
        const val CONTENT_URL_INDEX = 2
    }

    private val args: ModuleCourseContentActivityArgs by lazy {
        intent.getParcelableExtra(KEY_ARG) ?: run {
            val data: Uri = intent.data!!
            val paths = data.pathSegments
            val courseId = paths[ModuleCourseContentActivity.COURSE_URL_INDEX]
            val pathwayId = paths[ModuleCourseContentActivity.PATHWAY_URL_INDEX].toInt()
            val contentId = paths[ModuleCourseContentActivity.CONTENT_URL_INDEX]
            ModuleCourseContentActivityArgs(courseId.toInt(), contentId)
        }
    }


//    private val viewModel: ModuleCourseContentViewModel by sharedViewModel()
    private lateinit var mAdapter: CourseContentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_module_course_content)

        setSupportActionBar(mBinding.moduleContentToolbar)
        mBinding.buttonBack.setOnClickListener { finish() }



        mBinding.bottomNavigationExercise.setNavigationActions(
            {
             Toast.makeText(this, "Previous", Toast.LENGTH_SHORT).show()
            },
            {
                Toast.makeText(this, "Next", Toast.LENGTH_SHORT).show()
            })

        initRecyclerViewExerciseList()
//        viewModel.viewState.observe(this, {
//        })
//
//        viewModel.viewEvents.observe(this, {
//
//        })


    }


    private fun initRecyclerViewExerciseList() {
        mAdapter = CourseContentAdapter {
//            viewModel.handle(CourseContentActivityViewActions.ContentListItemSelected(it.id))
        }

        val layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mBinding.recyclerviewCourseExerciseList.layoutManager = layoutManager
        mBinding.recyclerviewCourseExerciseList.adapter = mAdapter
        mBinding.recyclerviewCourseExerciseList.itemAnimator = null
        mBinding.recyclerviewCourseExerciseList.addItemDecoration(
            ListSpacingDecoration(
                0,
                resources.getDimensionPixelSize(R.dimen.dimen_course_content_margin)
            )
        )
    }
}