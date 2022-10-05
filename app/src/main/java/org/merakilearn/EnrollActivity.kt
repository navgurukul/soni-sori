package org.merakilearn

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.core.app.TaskStackBuilder
import androidx.core.view.isVisible
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_discover_enroll.*
import kotlinx.android.synthetic.main.content_class_detail.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.merakilearn.datasource.UserRepo
import org.merakilearn.ui.onboarding.OnBoardingActivity
import org.navgurukul.learn.ui.common.toast

@Parcelize
data class EnrollActivityArgs(
    val classId: Int,
    val isEnrolled: Boolean
) : Parcelable

class EnrollActivity : AppCompatActivity() {

    companion object {
        private const val ENROLL_ACTIVITY_ARGS = "enroll_activity_args"

        fun start(
            context: Context,
            classId: Int,
            isEnrolled: Boolean
        ) {
            val intent = Intent(context, EnrollActivity::class.java)
            intent.putExtra(ENROLL_ACTIVITY_ARGS, EnrollActivityArgs(classId, isEnrolled))
            context.startActivity(intent)
        }
    }

    private val viewModel: EnrollViewModel by viewModel(parameters = {
        parametersOf(
            classId, isEnrolled
        )
    })

    private var classId = 0
    private var isEnrolled = false
    private var menuId: Int? = null

    private val userRepo: UserRepo by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discover_enroll)

        if (userRepo.isUserLoggedIn()) {
            if (intent.hasExtra(ENROLL_ACTIVITY_ARGS)) {
                val enrollActivityArgs =
                    intent.getParcelableExtra<EnrollActivityArgs>(ENROLL_ACTIVITY_ARGS)
                        ?: run {
                            finish()
                            return@onCreate
                        }
                classId = enrollActivityArgs.classId
                isEnrolled = enrollActivityArgs.isEnrolled
            } else {
                val action: String? = intent?.action
                val data: Uri? = intent?.data
                val uriString = data.toString()
                if (action == Intent.ACTION_VIEW && uriString.contains("/class/")) {
                    classId = uriString.split("/").last().toInt()
                } else {
                    finish()
                    return
                }
            }
        } else {
            finish()
            OnBoardingActivity.restartApp(this, true)
            return
        }

        viewModel.viewState.observe(this, {
            updateState(it)
        })

        viewModel.viewEvents.observe(this) {
            when (it) {
                is EnrollViewEvents.ShowToast -> toast(it.toastText)
                is EnrollViewEvents.OpenLink -> startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(it.link)
                    )
                )
            }
        }

        primary_action.setOnClickListener {
            viewModel.handle(EnrollViewActions.PrimaryAction)
        }

        initToolBar()
    }

    private fun updateState(it: EnrollViewState) {
        progress_bar_button.isVisible = it.isLoading
        it.about?.let {
            tvAbout.isVisible = true
            tvAboutTitle.isVisible = true
            tvAbout.text = it
        } ?: run {
            tvAbout.isVisible = false
            tvAboutTitle.isVisible = false
        }

        it.details?.let {
            tvClassDetail.isVisible = true
            tvClassDetailTitle.isVisible = true
            tvClassDetail.text = it
        } ?: run {
            tvClassDetail.isVisible = false
            tvClassDetailTitle.isVisible = false
        }

        it.type?.let {
            tvClassType.isVisible = true
            tvClassType.text = it
        } ?: run {
            tvClassType.isVisible = false
        }

        it.rules?.let {
            tvSpecialInstruction.isVisible = true
            tvSpecialInstructionTitle.isVisible = true
            tvSpecialInstruction.loadFromText(it)
        } ?: run {
            tvSpecialInstruction.isVisible = false
            tvSpecialInstructionTitle.isVisible = false
        }

        it.primaryAction?.let {
            primary_action.isVisible = true
            primary_action.text = it
        } ?: kotlin.run {
            primary_action.isVisible = false
        }

        it.language?.let {
            tvClassLanguage.isVisible = true
            tvClassLanguage.text = it
        } ?: run {
            tvClassLanguage.isVisible = false
        }

        it.title?.let {
            tv_title.text = it
        }

        it.primaryActionBackgroundColor?.let {
            primary_action.setBackgroundColor(it)
        }

        menuId = it.menuId

        invalidateOptionsMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuId?.let { menuInflater.inflate(it ,menu) }
        return super.onCreateOptionsMenu(menu)
    }


    private fun initToolBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
            it.setDisplayShowTitleEnabled(false);
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            navigateUp()
        }
        if (item.itemId == R.id.drop_out) {
            viewModel.handle(EnrollViewActions.DropOut)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        navigateUp()
    }

    private fun navigateUp() {
        val upIntent = NavUtils.getParentActivityIntent(this) ?: run {
            finish()
            return
        }

        if (NavUtils.shouldUpRecreateTask(this, upIntent) || isTaskRoot) {
            TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities()
        } else {
            NavUtils.navigateUpTo(this, upIntent)
        }
    }

}