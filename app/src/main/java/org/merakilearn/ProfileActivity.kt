package org.merakilearn

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.merakilearn.databinding.ActivityProfileBinding
import org.merakilearn.datasource.network.model.LoginResponse
import org.merakilearn.ui.onboarding.LoginViewModel
import org.merakilearn.util.AppUtils
import org.navgurukul.chat.core.glide.GlideApp
import org.navgurukul.learn.ui.common.toast

class ProfileActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityProfileBinding
    private val viewModel: LoginViewModel by viewModel()
    private lateinit var user: LoginResponse.User
    private var isFromDeepLink = false

    companion object {
        fun launch(context: Context) {
            val intent = Intent(context, ProfileActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        if (AppUtils.isUserLoggedIn(this) && !AppUtils.isFakeLogin(this)) {
            initIntentFilter()
            initLinkButton()
            AppUtils.getCurrentUser(this)?.let {
                user = it
                initToolBar()
                mBinding.user = user
                mBinding.tvAppVersion.text =
                    getString(R.string.app_version, BuildConfig.VERSION_NAME)
            }?.run {
                OnBoardingActivity.restartApp(this@ProfileActivity, OnBoardingActivityArgs(true))
            }
        } else {
            OnBoardingActivity.restartApp(this, OnBoardingActivityArgs(true))
        }

    }

    private fun initIntentFilter() {
        val action: String? = intent?.action
        val data: Uri? = intent?.data
        handleDeepLink(action, data)
    }

    private fun handleDeepLink(action: String?, data: Uri?) {
        val uriString = data.toString()
        if (action == Intent.ACTION_VIEW) {
            if (uriString.endsWith("/me")) {
                isFromDeepLink = true
            }
        }
    }

    private fun initToolBar() {
        mBinding.idHeader.ivBackButton.setOnClickListener {
            finishCurrent()
        }
        mBinding.ivEdit.setOnClickListener {
            if (AppUtils.isFakeLogin(this)) {
                toast(getString(R.string.please_connect))
            } else {
                mBinding.linkAccount.visibility = View.VISIBLE
                mBinding.llProfileEdit.visibility = View.VISIBLE
            }
        }
        mBinding.idHeader.ivLogOut.setOnClickListener {
            showLogOutDialog()
        }

        val requestOptions = RequestOptions()
            .centerCrop()
            .transform(CircleCrop())

        val thumbnail= GlideApp.with(this)
            .load(R.drawable.illus_default_avatar)
            .apply(requestOptions)

        GlideApp.with(mBinding.ivProfile)
            .load(user.profilePicture)
            .apply(requestOptions)
            .thumbnail(thumbnail)
            .transform(CircleCrop())
            .into(mBinding.ivProfile)
    }

    private fun finishCurrent() {
        if (isFromDeepLink) {
            MainActivity.launch(this)
        } else
            finish()
    }

    private fun showLogOutDialog() {
        AlertDialog.Builder(this).setMessage(getString(R.string.want_log_out))
            .setPositiveButton(
                getString(R.string.okay)
            ) { dialog, _ ->
                viewModel.logOut().observe(this, Observer {
                    if (it) {
                        dialog.dismiss()
                        OnBoardingActivity.restartApp(
                            this, OnBoardingActivityArgs(
                                clearNotification = true
                            )
                        )
                    }
                })
            }.setNegativeButton(
                getString(R.string.cancel)
            ) { dialog, _ ->
                dialog.dismiss()
            }.create().show()
    }

    private fun initLinkButton() {
        mBinding.linkAccount.setOnClickListener {
            updateProfile()
        }
    }

    private fun updateProfile() {
        toggleProgressBarVisibility(View.VISIBLE)
        viewModel.updateProfile(user).observe(this, Observer {
            toggleProgressBarVisibility(View.GONE)
            mBinding.llProfileEdit.visibility = View.GONE
            if (it) {
                toast(getString(R.string.profile_updated_successfully))
                finish()
            } else
                toast(getString(R.string.unable_to_update))
        })
    }

    private fun toggleProgressBarVisibility(visibiltiy: Int) {
        mBinding.progressBarButton.visibility = visibiltiy
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishCurrent()
    }
}