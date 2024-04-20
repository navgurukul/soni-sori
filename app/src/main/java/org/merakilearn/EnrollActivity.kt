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
import org.merakilearn.databinding.ActivityDiscoverEnrollBinding
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
    private lateinit var binding: ActivityDiscoverEnrollBinding

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
        binding = ActivityDiscoverEnrollBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        binding.primaryAction.setOnClickListener {
            viewModel.handle(EnrollViewActions.PrimaryAction)
        }

        initToolBar()
    }

    private fun updateState(it: EnrollViewState) {
        binding.progressBarButton.isVisible = it.isLoading
        it.about?.let {
            binding.classDetail.tvAbout.isVisible = true
            binding.classDetail.tvAboutTitle.isVisible = true
            binding.classDetail.tvAbout.text = it
        } ?: run {
            binding.classDetail.tvAbout.isVisible = false
            binding.classDetail.tvAboutTitle.isVisible = false
        }

        it.details?.let {
            binding.classDetail.tvClassDetail.isVisible = true
            binding.classDetail.tvClassDetailTitle.isVisible = true
            binding.classDetail.tvClassDetail.text = it
        } ?: run {
            binding.classDetail.tvClassDetail.isVisible = false
            binding.classDetail.tvClassDetailTitle.isVisible = false
        }

        it.type?.let {
            binding.tvClassType.isVisible = true
            binding.tvClassType.text = it
        } ?: run {
            binding.tvClassType.isVisible = false
        }

        it.rules?.let {
            binding.classDetail.tvSpecialInstruction.isVisible = true
            binding.classDetail.tvSpecialInstructionTitle.isVisible = true
            binding.classDetail.tvSpecialInstruction.loadFromText(it)
        } ?: run {
            binding.classDetail.tvSpecialInstruction.isVisible = false
            binding.classDetail.tvSpecialInstructionTitle.isVisible = false
        }

        it.primaryAction?.let {
            binding.primaryAction.isVisible = true
            binding.primaryAction.text = it
        } ?: kotlin.run {
            binding.primaryAction.isVisible = false
        }

        it.language?.let {
            binding.tvClassLanguage.isVisible = true
            binding.tvClassLanguage.text = it
        } ?: run {
            binding.tvClassLanguage.isVisible = false
        }

        it.title?.let {
            binding.tvTitle.text = it
        }

        it.primaryActionBackgroundColor?.let {
           binding.primaryAction.setBackgroundColor(it)
        }

        menuId = it.menuId

        invalidateOptionsMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuId?.let { menuInflater.inflate(it ,menu) }
        return super.onCreateOptionsMenu(menu)
    }


    private fun initToolBar() {
        setSupportActionBar(binding.toolbar)
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