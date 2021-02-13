package org.navgurukul.learn.ui.learn

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
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
import org.navgurukul.learn.databinding.ActivityCourseSlugDetailBinding
import org.navgurukul.learn.ui.learn.adapter.CourseExerciseAdapter
import org.navgurukul.learn.ui.learn.adapter.ExerciseSlugAdapter
import org.navgurukul.learn.util.LearnUtils

import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.tasks.OnFailureListener
import com.google.android.play.core.tasks.OnSuccessListener
import org.navgurukul.learn.util.TypingGuruPreferenceManager


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
    private var masterData: MutableList<Exercise> = mutableListOf()
    private val merakiNavigator: MerakiNavigator by inject()
    private lateinit var splitInstallManager : SplitInstallManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_course_slug_detail)
        // Instantiate an instance of SplitInstallManager for the dynamic feature module
        splitInstallManager = SplitInstallManagerFactory.create(this);
        if (LearnUtils.isUserLoggedIn(this)) {
            parseIntentData()
        } else
            merakiNavigator.restartApp(this, true)

    }

    private fun parseIntentData() {
        if (intent.hasExtra(ARG_KEY_CURRENT_STUDY)) {
            currentStudy = intent.getSerializableExtra(ARG_KEY_CURRENT_STUDY) as CurrentStudy
            renderUI()
        } else {
            val action: String? = intent?.action
            val data: Uri? = intent?.data
            val uriString = data.toString()
            if (action == Intent.ACTION_VIEW) {
                if (uriString.contains("/exercise/")) {
                    val courseId = data?.pathSegments?.get(1)
                    val exerciseId = uriString.split("/").last()
                    fetchExerciseDataAndShow(exerciseId, courseId.toString())
                }
            } else {
                renderUI()
            }
        }
    }

    private fun fetchExerciseDataAndShow(exerciseId: String, courseId: String) {
        mBinding.progressBar.visibility = View.VISIBLE
        viewModel.fetchExerciseSlug(exerciseId, courseId, true)
            .observe(this, Observer {
                mBinding.progressBar.visibility = View.GONE
                if (null != it && it.isNotEmpty()) {
                    currentStudy = CurrentStudy(
                        courseId = courseId,
                        courseName = it.firstOrNull()?.courseName,
                        exerciseSlugName = it.firstOrNull()?.slug!!,
                        exerciseName = it.firstOrNull()?.name,
                        exerciseId = exerciseId
                    )
                    renderUI()
                }
            })
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
        initRecyclerViewSlidingPanel()
        mBinding.recyclerViewSlug.visibility = View.GONE
    }

    private fun hidePanel() {
        mBinding.slideComponent.slide.visibility = View.GONE
        mBinding.slideComponent.slide.closePane()
        mBinding.recyclerViewSlug.visibility = View.VISIBLE
    }


    private fun renderUI() {
        mBinding.header.title = currentStudy.exerciseName
        initUIElement()
        initRecyclerViewSlidingPanel()
        initContentRV()
        saveCourseExercise()
        initSwipeRefresh()
    }

    private fun initContentRV() {
        slugAdapter = ExerciseSlugAdapter {
            if (it.type == ExerciseSlugAdapter.TYPE_PYTHON) {
                val code = ExerciseSlugAdapter.parsePythonCode(it)
                if (!code.isNullOrBlank())
                    merakiNavigator.openPlayground(this, code)
            } else if (it.type == ExerciseSlugAdapter.TYPE_TRY_TYPING || it.type == ExerciseSlugAdapter.TYPE_PRACTICE_TYPING) {
                loadTypingTutor(it)
            }
        }
        val layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.recyclerViewSlug.layoutManager = layoutManager
        mBinding.recyclerViewSlug.adapter = slugAdapter
        fetchSlugContent(currentStudy.exerciseId, false)

    }

    private fun loadTypingTutor(it: Exercise.ExerciseSlugDetail) {
        // Builds a request to install the feature1 module
        val request = SplitInstallRequest
                .newBuilder() // You can download multiple on demand modules per
                // request by invoking the following method for each
                // module you want to install.
                .addModule("typing")
                .build()

        // Begin the installation of the feature1 module and handle success/failure
        splitInstallManager
                .startInstall(request)
                .addOnSuccessListener(object : OnSuccessListener<Int?> {
                    override fun onSuccess(integer: Int?) {
                        // Module download successful
                        if (TypingGuruPreferenceManager.instance().iWebViewShown()) {
                            val intent = Intent()
                                .setClassName(packageName, "org.navgurukul.typingguru.ui.KeyboardActivity")
                            intent.putExtra("content", it.value as ArrayList<String>)
                            intent.putExtra("type", it.type)
                            startActivity(intent)
                        } else {
                            TypingGuruPreferenceManager.instance().setWebViewDisplayStatus(true)
                            val intent = Intent()
                                .setClassName(packageName, "org.navgurukul.typingguru.ui.WebViewActivity")
                            startActivity(intent)
                        }
                    }
                })
                .addOnFailureListener(object : OnFailureListener {
                    override fun onFailure(e: Exception) {
                        // Module download failed; handle the error here
                        Toast.makeText(
                                applicationContext,
                                "Couldn't download typing guru: " + e.message,
                                Toast.LENGTH_LONG
                        ).show()
                        e.printStackTrace()
                    }
                })
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
        fetchAndSetMasterData()
    }

    private fun fetchAndSetMasterData() {
        viewModel.fetchCourseExerciseData(currentStudy.courseId).observe(this, Observer {
            mBinding.progressBar.visibility = View.VISIBLE
            if (null != it && it.isNotEmpty()) {
                mBinding.progressBar.visibility = View.GONE
                masterData = it as MutableList<Exercise>
                mAdapter.submitList(masterData)
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
        CourseDetailActivity.start(this, currentStudy.courseId, currentStudy.courseName ?: "")
        finish()
    }

}