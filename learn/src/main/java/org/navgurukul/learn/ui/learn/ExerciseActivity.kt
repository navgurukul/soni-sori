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
import kotlinx.android.parcel.Parcelize
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.merakilearn.core.extentions.KEY_ARG
import org.merakilearn.core.extentions.toBundle
import org.merakilearn.core.navigator.MerakiNavigator
import org.navgurukul.commonui.platform.ListSpacingDecoration
import org.navgurukul.learn.R
import org.navgurukul.learn.databinding.ActivityExerciseBinding
import org.navgurukul.learn.ui.common.toast
import org.navgurukul.learn.ui.learn.adapter.CourseExerciseAdapter
import org.navgurukul.learn.util.LearnUtils

@Parcelize
data class ExerciseActivityArgs(val courseId: String, val pathwayId: Int) : Parcelable

class ExerciseActivity : AppCompatActivity(), ExerciseFragment.ExerciseNavigationClickListener {

    companion object {
        fun start(context: Context, courseId: String, pathwayId: Int) {
            val intent = Intent(context, ExerciseActivity::class.java).apply {
                putExtras(ExerciseActivityArgs(courseId, pathwayId).toBundle()!!)
            }
            context.startActivity(intent)
        }

        const val PATHWAY_URL_INDEX = 1
        const val COURSE_URL_INDEX = 3
    }

    private val args: ExerciseActivityArgs by lazy {
        intent.getParcelableExtra(KEY_ARG) ?: run {
            val data: Uri = intent.data!!
            val paths = data.pathSegments
            val courseId = paths[COURSE_URL_INDEX]
            val pathwayId = paths[PATHWAY_URL_INDEX].toInt()
            ExerciseActivityArgs(courseId, pathwayId)
        }
    }

    private lateinit var mBinding: ActivityExerciseBinding
    private val viewModel: ExerciseActivityViewModel by viewModel(
        parameters = { parametersOf(args.courseId, args.pathwayId) }
    )
    private lateinit var mAdapter: CourseExerciseAdapter
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
                viewModel.handle(ExerciseActivityViewActions.PrevNavigationClicked)
            },
            {
                viewModel.handle(ExerciseActivityViewActions.ExerciseMarkedCompleted)
                viewModel.handle(ExerciseActivityViewActions.NextNavigationClicked)
            }
        )

        initRecyclerViewExerciseList()


        viewModel.viewEvents.observe(this, {
            when (it) {
                is ExerciseActivityViewEvents.ShowToast -> toast(it.toastText)
                is ExerciseActivityViewEvents.ShowExerciseFragment -> {
                    launchExerciseFragment(
                        it.isFirst,
                        it.isLast,
                        it.isCompleted,
                        it.courseId,
                        it.exerciseId,
                        it.navigation
                    )

                    mBinding.bottomNavigationExercise.updateNavButtons(it.isFirst)
                }
                ExerciseActivityViewEvents.FinishActivity -> finish()
            }
        })

        viewModel.viewState.observe(this, {
            mBinding.progressBar.visibility = if (it.isLoading) View.VISIBLE else View.GONE

            if (!it.isCourseCompleted) {
                mAdapter.submitList(it.exerciseList) {
                    mBinding.recyclerviewCourseExerciseList.scrollToPosition(it.currentExerciseIndex)
                }
                mBinding.tvCourseTitle.text = it.currentCourseTitle

                mBinding.courseCompletedView.root.visibility = View.GONE
                mBinding.appBarExercise.visibility = View.VISIBLE
                mBinding.exerciseContentContainer.visibility = View.VISIBLE
            } else {
                showCompletionScreen(it.nextCourseTitle, it.currentCourseTitle)
            }
        })
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
            viewModel.handle(ExerciseActivityViewActions.OnNextCourseClicked)
        }
    }

    private fun initRecyclerViewExerciseList() {
        mAdapter = CourseExerciseAdapter {
            viewModel.handle(ExerciseActivityViewActions.ExerciseListItemSelected(it.id))
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
                ExerciseFragment.newInstance(isFirst, isLast, isCompleted, courseId, exerciseId),
                ExerciseFragment.TAG
            )
        }
    }

    override fun onMarkCompleteClick() {
        viewModel.handle(ExerciseActivityViewActions.ExerciseMarkedCompleted)
    }

}