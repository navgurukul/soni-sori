package org.navgurukul.learn.ui.learn

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.play.core.assetpacks.cu
import kotlinx.android.parcel.Parcelize
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.merakilearn.core.extentions.KEY_ARG
import org.merakilearn.core.extentions.toBundle
import org.merakilearn.core.navigator.MerakiNavigator
import org.navgurukul.commonui.platform.ListSpacingDecoration
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.CourseContentType
import org.navgurukul.learn.databinding.ActivityExerciseBinding
import org.navgurukul.learn.ui.common.toast
import org.navgurukul.learn.ui.learn.adapter.CourseContentAdapter
import org.navgurukul.learn.util.LearnUtils

@Parcelize
data class CourseContentActivityArgs(val courseId: String, val pathwayId: Int, val contentId: String?= null) : Parcelable

class CourseContentActivity : AppCompatActivity(){

    companion object {
        fun start(context: Context, courseId: String, pathwayId: Int, contentId: String? = null) {
            val intent = Intent(context, CourseContentActivity::class.java).apply {
                putExtras(CourseContentActivityArgs(courseId, pathwayId, contentId).toBundle()!!)
            }
            context.startActivity(intent)
        }

        const val PATHWAY_URL_INDEX = 1
        const val COURSE_URL_INDEX = 3
        const val CONTENT_URL_INDEX = 2
    }

    private val args: CourseContentActivityArgs by lazy {
        intent.getParcelableExtra(KEY_ARG) ?: run {
            val data: Uri = intent.data!!
            val paths = data.pathSegments
            val courseId = paths[COURSE_URL_INDEX]
            val pathwayId = paths[PATHWAY_URL_INDEX].toInt()
            val contentId = paths[CONTENT_URL_INDEX]
            CourseContentActivityArgs(courseId, pathwayId, contentId)
        }
    }

    private lateinit var mBinding: ActivityExerciseBinding
    private val viewModel: CourseContentActivityViewModel by viewModel(
        parameters = { parametersOf(args.courseId, args.pathwayId, args.contentId) }
    )
    private lateinit var mAdapter: CourseContentAdapter
    private val merakiNavigator: MerakiNavigator by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_exercise)
        // Instantiate an instance of SplitInstallManager for the dynamic feature module
        if (!LearnUtils.isUserLoggedIn(this)) {
            merakiNavigator.restartApp(this, true)
        }

        setSupportActionBar(mBinding.exerciseToolbar)
        mBinding.buttonBack.setOnClickListener { finish() }

        mBinding.bottomNavigationExercise.setNavigationActions(
            {
                viewModel.handle(CourseContentActivityViewActions.PrevNavigationClicked)
            },
            {
                if(!isCurrentContentAssessment())
                    viewModel.handle(CourseContentActivityViewActions.ContentMarkedCompleted)
                viewModel.handle(CourseContentActivityViewActions.NextNavigationClicked)
            }
        )

        initRecyclerViewExerciseList()


        viewModel.viewEvents.observe(this) {
            when (it) {
                is CourseContentActivityViewEvents.ShowToast -> toast(it.toastText)
                is CourseContentActivityViewEvents.ShowExerciseFragment -> {
                    launchExerciseFragment(
                        it.isFirst,
                        it.isLast,
                        it.isCompleted,
                        it.courseId,
                        it.contentId,
                        it.courseContentType,
                        it.navigation
                    )

                    mBinding.bottomNavigationExercise.updateNavButtons(it.isFirst)
                }
                is CourseContentActivityViewEvents.ShowClassFragment -> {
                    launchClassFragment(
                        it.isFirst,
                        it.isLast,
                        it.isCompleted,
                        it.courseId,
                        it.contentId,
                        it.courseContentType,
                        it.navigation
                    )

                    mBinding.bottomNavigationExercise.updateNavButtons(it.isFirst)
                }
                is CourseContentActivityViewEvents.ShowAssessmentFragment -> {
                    launchAssessmentFragment(
                        it.isFirst,
                        it.isLast,
                        it.isCompleted,
                        it.courseId,
                        it.contentId,
                        it.courseContentType,
                        it.navigation
                    )

                    mBinding.bottomNavigationExercise.updateNavButtons(it.isFirst)
                }
                CourseContentActivityViewEvents.FinishActivity -> finish()
            }
        }

        viewModel.viewState.observe(this) {
            mBinding.progressBar.visibility = if (it.isLoading) View.VISIBLE else View.GONE

            if (!it.isCourseCompleted) {
                mAdapter.submitList(it.courseContentList) {
                    mBinding.recyclerviewCourseExerciseList.smoothScrollToPosition(it.currentContentIndex)
                }
                mBinding.tvCourseTitle.text = it.currentCourseTitle

                mBinding.courseCompletedView.root.visibility = View.GONE
                mBinding.appBarExercise.visibility = View.VISIBLE
                mBinding.exerciseContentContainer.visibility = View.VISIBLE
            } else {
                showCompletionScreen(it.nextCourseTitle, it.currentCourseTitle)
            }
        }
    }

    private fun isCurrentContentAssessment(): Boolean {
        val currState = viewModel.viewState.value
        return currState?.let{
            val idx = it.currentContentIndex
            val list = it.courseContentList
            if(idx > -1 && idx < list.size)
                return@let list[idx].courseContentType == CourseContentType.assessment
            return@let false
        }?:false
    }

    private fun showCompletionScreen(
        nextCourseTitle: String,
        currentCourseTitle: String
    ) {
        mBinding.courseCompletedView.root.visibility = View.VISIBLE
        mBinding.appBarExercise.visibility = View.GONE
        mBinding.exerciseContentContainer.visibility = View.GONE

        mBinding.courseCompletedView.tvCompletedCourseMsg.text =
            getString(R.string.course_completed_message, currentCourseTitle)

        mBinding.bottomNavigationExercise.setMainButton(nextCourseTitle) {
            viewModel.handle(CourseContentActivityViewActions.OnNextCourseClicked)
        }
    }

    private fun initRecyclerViewExerciseList() {
        mAdapter = CourseContentAdapter {
            viewModel.handle(CourseContentActivityViewActions.ContentListItemSelected(it.id))
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

    private fun launchExerciseFragment(
        isFirst: Boolean = false,
        isLast: Boolean = false,
        isCompleted: Boolean = false,
        courseId: String,
        exerciseId: String,
        courseContentType: CourseContentType,
        navigation: ExerciseNavigation?
    ) {
        supportFragmentManager.commit {
            val enter = when (navigation) {
                ExerciseNavigation.PREV -> android.R.anim.slide_in_left
                ExerciseNavigation.NEXT -> R.anim.slide_in_to_left
                null -> android.R.anim.fade_in
            }
            setCustomAnimations(
                enter, 0
            )
            replace(
                R.id.exerciseContentContainer,
                ExerciseFragment.newInstance(isFirst, isLast, isCompleted, courseId, exerciseId, courseContentType),
                ExerciseFragment.TAG
            )
        }
    }

    private fun launchClassFragment(
        isFirst: Boolean = false,
        isLast: Boolean = false,
        isCompleted: Boolean = false,
        courseId: String,
        classId: String,
        courseContentType: CourseContentType,
        navigation: ExerciseNavigation?
    ) {
        supportFragmentManager.commit {
            val enter = when (navigation) {
                ExerciseNavigation.PREV -> android.R.anim.slide_in_left
                ExerciseNavigation.NEXT -> R.anim.slide_in_to_left
                null -> android.R.anim.fade_in
            }
            setCustomAnimations(
                enter, 0
            )
            replace(
                R.id.exerciseContentContainer,
                ClassFragment.newInstance(isFirst, isLast, isCompleted, courseId, classId, courseContentType),
                ClassFragment.TAG
            )
        }
    }

    private fun launchAssessmentFragment(
        isFirst: Boolean = false,
        isLast: Boolean = false,
        isCompleted: Boolean = false,
        courseId: String,
        assessmentId : String,
        courseContentType: CourseContentType,
        navigation: ExerciseNavigation?
    ){
        supportFragmentManager.commit {
            val enter = when(navigation){
                ExerciseNavigation.PREV -> android.R.anim.slide_in_left
                ExerciseNavigation.NEXT -> R.anim.slide_in_to_left
                null -> android.R.anim.fade_in
            }
            setCustomAnimations(
                enter,0
            )
            replace(
                R.id.exerciseContentContainer,
                AssessmentFragment.newInstance(isFirst, isLast, isCompleted, courseId, assessmentId, courseContentType),
                AssessmentFragment.TAG
            )
        }
    }
}