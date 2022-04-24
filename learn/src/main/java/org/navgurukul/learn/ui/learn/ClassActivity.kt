package org.navgurukul.learn.ui.learn

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.class_course_detail.*
import kotlinx.android.synthetic.main.fragment_class.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.merakilearn.core.extentions.KEY_ARG
import org.merakilearn.core.extentions.capitalizeWords
import org.merakilearn.core.extentions.toBundle
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.CourseClassContent
import org.navgurukul.learn.courses.db.models.displayableLanguage
import org.navgurukul.learn.courses.db.models.timeDateRange
import org.navgurukul.learn.databinding.ActivityClassBinding
import org.navgurukul.learn.ui.common.toast
import java.util.*

@Parcelize
data class ClassActivityArgs(val classContent: CourseClassContent): Parcelable

class ClassActivity: AppCompatActivity(){

    companion object {
        fun start(context: Context, classContent: CourseClassContent) {
            val intent = Intent(context, CourseContentActivity::class.java).apply {
                putExtras(ClassActivityArgs(classContent).toBundle()!!)
            }
            context.startActivity(intent)
        }
    }

    private val args: ClassActivityArgs? by lazy {
        intent.extras?.getParcelable(KEY_ARG)
    }

    private lateinit var mBinding: ActivityClassBinding
    private val viewModel: EnrollViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupToolbar()

        showClassDetails(intent.extras?.getParcelable<ClassActivityArgs>(KEY_ARG))

        args?.classContent?.let {
            viewModel.handle(EnrollViewActions.RequestPageLoad(it))
        }

        viewModel.viewState.observe(this, {
            it?.let {
                updateState(it)
            }
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
    }

    private fun updateState(it: EnrollViewState) {
        it.primaryActionBackgroundColor?.let {
            mBinding.tvBtnJoin.setBackgroundColor(it)
        }
        it.primaryAction?.let {
            mBinding.tvBtnJoin.text = it
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(mBinding.classToolbar)
        supportActionBar?.let {
            it.setDisplayShowTitleEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_navigate_back)
        }
    }

    private fun showClassDetails(args: ClassActivityArgs?) {
        args?.let { arg ->
            args.classContent.also {
                supportActionBar?.title = it.subTitle
                mBinding.tvClassType.text = it.type.name.capitalizeWords()
                mBinding.tvClassLanguage.text = it.displayableLanguage()
                mBinding.tvDate.text = it.timeDateRange()
                mBinding.tvFacilatorName.text = it.facilitator?.name

                mBinding.tvBtnJoin.setOnClickListener {
                    viewModel.handle(EnrollViewActions.PrimaryAction(args.classContent))
                }
            }

        }
    }

}