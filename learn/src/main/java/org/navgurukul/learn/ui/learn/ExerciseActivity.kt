package org.navgurukul.learn.ui.learn

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.merakilearn.core.navigator.MerakiNavigator
import org.navgurukul.commonui.platform.ListSpacingDecoration
import org.navgurukul.learn.R
import org.navgurukul.learn.databinding.ActivityExerciseBinding
import org.navgurukul.learn.ui.common.toast
import org.navgurukul.learn.ui.learn.adapter.CourseExerciseAdapter
import org.navgurukul.learn.util.LearnUtils


class ExerciseActivity : AppCompatActivity(), ExerciseFragment.ExerciseNavigationClickListener {

    companion object {
        private const val ARG_KEY_COURSE_ID = "arg_course_id"
        private const val ARG_KEY_PATHWAY_ID = "arg_pathway_id"

        fun start(context: Context, courseId: String, pathwayId: Int) {
            val intent = Intent(context, ExerciseActivity::class.java)
            intent.putExtra(ARG_KEY_COURSE_ID, courseId)
            intent.putExtra(ARG_KEY_PATHWAY_ID, pathwayId)
            context.startActivity(intent)
        }
    }

    private val courseId: String by lazy {
        intent.getStringExtra(ARG_KEY_COURSE_ID) ?: run {
            val action: String? = intent?.action
            val data: Uri? = intent?.data
            val uriString = data.toString()
            if (action == Intent.ACTION_VIEW && uriString.contains("/course/")) {
                uriString.split("/").last()
            } else {
                ""
            }
        }
    }

    private val pathwayId: Int by lazy {
        intent.getIntExtra(ARG_KEY_PATHWAY_ID, 0)
    }

    private lateinit var mBinding: ActivityExerciseBinding
    private val viewModel: ExerciseActivityViewModel by viewModel(
        parameters = { parametersOf(courseId, pathwayId) }
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

                    setExerciseNavigationBarView(true, it.isFirst)
                }
            }
        })

        viewModel.viewState.observe(this, {
            mBinding.progressBar.visibility = if (it.isLoading) View.VISIBLE else View.GONE

            showCompletionScreen(it.isCourseCompleted, it.nextCourseTitle, it.currentCourseTitle)

            if (!it.isCourseCompleted) {
                mAdapter.submitList(it.exerciseList)
                mBinding.tvCourseTitle.text = it.currentCourseTitle
            }

        })

    }

    private fun setExerciseNavigationBarView(isVisibile: Boolean, isFirst: Boolean = false) {
        if(isVisibile) {
            mBinding.bottomNavigationExercise.visibility = View.VISIBLE
            mBinding.bottomNavigationExercise.setView(isFirst)
        }else{
            mBinding.bottomNavigationExercise.visibility = View.GONE
        }
    }

    private fun showCompletionScreen(
        courseCompleted: Boolean,
        nextCourseTitle: String,
        currentCourseTitle: String
    ) {
        if (courseCompleted) {
            mBinding.courseCompletedView.root.visibility = View.VISIBLE
            mBinding.appBarExercise.visibility = View.GONE
            mBinding.exerciseContentContainer.visibility = View.GONE

            mBinding.courseCompletedView.tvCompletedCourseMsg.text =
                getString(R.string.course_completed_message, currentCourseTitle)
            mBinding.courseCompletedView.bottomNavigationLayout.setMainButton(
                getString(
                    R.string.next_course_message,
                    nextCourseTitle
                ),
                {
                    viewModel.handle(ExerciseActivityViewActions.OnNextCourseClicked)
                },
                true
            )
            setExerciseNavigationBarView(false)
        } else {
            mBinding.courseCompletedView.root.visibility = View.GONE
            mBinding.appBarExercise.visibility = View.VISIBLE
            mBinding.exerciseContentContainer.visibility = View.VISIBLE
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
                mBinding.recyclerviewCourseExerciseList.context,
                R.dimen.dimen_0_dp,
                R.dimen.dimen_course_content_margin
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
                //TODO test this after adding bottom nav bar and update animation
                ExerciseNavigation.NEXT -> android.R.anim.slide_in_left
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