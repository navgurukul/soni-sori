package org.navgurukul.learn.ui.learn

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.MenuItem
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import kotlinx.android.parcel.Parcelize
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.merakilearn.core.extentions.KEY_ARG
import org.merakilearn.core.extentions.setWidthPercent
import org.merakilearn.core.extentions.toBundle
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.CourseClassContent
import org.navgurukul.learn.courses.db.models.displayableLanguage
import org.navgurukul.learn.courses.db.models.sanitizedType
import org.navgurukul.learn.courses.db.models.timeDateRange
import org.navgurukul.learn.databinding.ActivityClassBinding
import org.navgurukul.learn.ui.common.toast
import java.util.*

@Parcelize
data class ClassActivityArgs(val classContent: CourseClassContent): Parcelable

class ClassActivity: AppCompatActivity(){

    companion object {
        fun start(context: Context, classContent: CourseClassContent) {
            val intent = Intent(context, ClassActivity::class.java).apply {
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

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_class)
        setupToolbar()

        viewModel.viewState.observe(this) {
            it?.let {
                updateState(it)
            }
        }

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

        args?.classContent?.let {
            viewModel.handle(EnrollViewActions.RequestPageLoad(it))
        }
        showClassDetails(args)
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
            it.setHomeAsUpIndicator(R.drawable.ic_arrow_left)
        }
    }

    private fun showClassDetails(args: ClassActivityArgs?) {
        args?.let { arg ->
            args.classContent.also {
                supportActionBar?.title = it.title
                mBinding.tvClassType.text = it.sanitizedType()
                mBinding.tvClassLanguage.text = it.displayableLanguage()
                mBinding.tvDate.text = it.timeDateRange()
                mBinding.tvFacilatorName.text = it.facilitator?.name

                mBinding.tvBtnJoin.setOnClickListener {
                    viewModel.handle(EnrollViewActions.PrimaryAction(args.classContent, true))
                }
                mBinding.btnDropOut.setOnClickListener {
                    showDropoutDialog()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun showDropoutDialog() {
                val alertLayout: View = getLayoutInflater().inflate(R.layout.dialog_dropout, null)
                val btnStay: View = alertLayout.findViewById(R.id.btnStay)
                val btnDroupOut: View = alertLayout.findViewById(R.id.btnDroupOut)
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder.setView(alertLayout)
                builder.setCancelable(true)
                val btAlertDialog: AlertDialog? = builder.create()
                btAlertDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
                btAlertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                btnStay.setOnClickListener {
                    btAlertDialog?.dismiss()
                }

                btnDroupOut.setOnClickListener {
                    args?.classContent?.let {
                        viewModel.handle(EnrollViewActions.DropOut(it, false))
                    }
                    btAlertDialog?.dismiss()
                }
                btAlertDialog?.show()
                btAlertDialog?.setWidthPercent(45)
        }
}
