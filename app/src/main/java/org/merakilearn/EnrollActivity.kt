package org.merakilearn

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.core.app.TaskStackBuilder
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_discover_enroll.*
import kotlinx.android.synthetic.main.content_class_detail.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.merakilearn.util.AppUtils
import org.navgurukul.learn.ui.common.toast
import kotlin.math.abs

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discover_enroll)

        if (AppUtils.isUserLoggedIn(this)) {
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
            OnBoardingActivity.restartApp(this, OnBoardingActivityArgs(true))
            return
        }

        viewModel.viewState.observe(this, Observer {
            updateState(it)
        })

        viewModel.viewEvents.observe(this, Observer {
            when (it) {
                is EnrollViewEvents.ShowToast -> toast(it.toastText)
                is EnrollViewEvents.OpenLink -> startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.link)))
            }
        })

        primary_action.setOnClickListener {
            viewModel.handle(EnrollViewActions.PrimaryAction)
        }

        secondary_action.setOnClickListener {
            viewModel.handle(EnrollViewActions.SecondaryAction)
        }

        app_bar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            val percentage: Double =
                abs(verticalOffset).toDouble() / collapsingToolbarLayout.height
            enroll_top_corner.isVisible = percentage <= 0.6
        })

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
        } ?: kotlin.run {
            tvSpecialInstruction.isVisible = false
            tvSpecialInstructionTitle.isVisible = false
        }

        it.primaryAction?.let {
            primary_action.isVisible = true
            primary_action.text = it
        } ?: kotlin.run {
            primary_action.isVisible = false
        }

        it.secondaryAction?.let {
            secondary_action.isVisible = true
            secondary_action.text = it
        } ?: kotlin.run {
            secondary_action.isVisible = false
        }

        it.language?.let {
            tvClassLanguage.isVisible = true
            if(it.toLowerCase().equals("hi")) {
                tvClassLanguage.text = "HINDI"
            }else if(it.toLowerCase().equals("en")) {
                tvClassLanguage.text = "ENGLISH"
            }else{
                tvClassLanguage.text ="OTHER"
            }
        } ?: kotlin.run {
            tvClassLanguage.isVisible = false
        }

        it.title?.let {
            collapsingToolbarLayout.title = it
        }
    }


    private fun initToolBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            navigateUp()
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