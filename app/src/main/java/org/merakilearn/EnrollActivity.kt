package org.merakilearn

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import br.tiagohm.markdownview.css.styles.Github
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.merakilearn.databinding.ActivityDiscoverEnrollBinding
import org.merakilearn.datasource.network.model.ClassesContainer
import org.merakilearn.ui.home.HomeViewModel
import org.merakilearn.util.AppUtils
import org.navgurukul.learn.ui.common.toast
import org.navgurukul.learn.ui.common.toolbarColor

class EnrollActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityDiscoverEnrollBinding

    companion object {
        private const val ARG_KEY_CLASS_ID = "arg_class"
        private const val ARG_KEY_IS_ENROLLED = "arg_is_enrolled"

        fun start(
            context: Context,
            classes: ClassesContainer.Classes,
            isEnrolled: Boolean
        ) {
            val intent = Intent(context, EnrollActivity::class.java)
            intent.putExtra(ARG_KEY_CLASS_ID, classes)
            intent.putExtra(ARG_KEY_IS_ENROLLED, isEnrolled)
            context.startActivity(intent)
        }
    }

    private lateinit var classes: ClassesContainer.Classes
    private var isEnrolled: Boolean = false
    private val viewModel: HomeViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_discover_enroll)
        parseIntentData()
        initToolBar()
        initExpandableToolBar()
        initButtonClick()
        initUI()
    }

    private fun initUI() {
        mBinding.classDetail.tvClassDetail.text = AppUtils.getClassSchedule(classes)
        mBinding.classDetail.tvAbout.text = AppUtils.getAboutClass(classes)
        if (!classes.rules?.en.isNullOrBlank()) {
            mBinding.classDetail.tvSpecialInstruction.apply {
                this.addStyleSheet(Github())
                this.loadMarkdown(classes.rules?.en)
            }
        }
    }

    private fun initButtonClick() {
        if (isEnrolled) {
            mBinding.enroll.text = getString(R.string.drop_out)
        }
        mBinding.enroll.setOnClickListener {
            mBinding.progressBarButton.visibility = View.VISIBLE
            viewModel.enrollToClass(classes.id!!, isEnrolled).observe(this, Observer {
                mBinding.progressBarButton.visibility = View.GONE
                if (isEnrolled) {
                    if (it) {
                        toast(getString(R.string.log_out_class))
                        finish()
                    } else {
                        toast(getString(R.string.unable_to_drop))
                    }
                } else {
                    if (it) {
                        toast(getString(R.string.enrolled))
                        finish()
                    } else {
                        toast(getString(R.string.unable_to_enroll))
                    }
                }
            })
        }
    }

    private fun parseIntentData() {
        if (intent.hasExtra(ARG_KEY_CLASS_ID)) {
            classes = intent.getSerializableExtra(ARG_KEY_CLASS_ID) as ClassesContainer.Classes
            isEnrolled = intent.getBooleanExtra(ARG_KEY_IS_ENROLLED, false)
        }
    }

    private fun initToolBar() {
        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    private fun initExpandableToolBar() {
        mBinding.toolbarLayout.setExpandedTitleColor(toolbarColor())
        mBinding.toolbarLayout.setCollapsedTitleTextColor(toolbarColor())
        mBinding.toolbarLayout.title = classes.title
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }

}