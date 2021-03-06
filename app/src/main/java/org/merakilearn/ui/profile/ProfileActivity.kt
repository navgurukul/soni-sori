package org.merakilearn.ui.profile

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.core.app.TaskStackBuilder
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_profile.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.merakilearn.OnBoardingActivity
import org.merakilearn.OnBoardingActivityArgs
import org.merakilearn.R
import org.merakilearn.core.navigator.MerakiNavigator
import org.merakilearn.databinding.ActivityProfileBinding
import org.merakilearn.ui.adapter.SavedFileAdapter
import org.merakilearn.util.AppUtils
import org.navgurukul.chat.core.glide.GlideApp
import org.navgurukul.commonui.platform.GridSpacingDecorator
import org.navgurukul.learn.ui.common.toast
import java.io.File


class ProfileActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityProfileBinding
    private val viewModel: ProfileViewModel by viewModel()
    private val merakiNavigator: MerakiNavigator by inject()

    companion object {
        fun launch(context: Context) {
            val intent = Intent(context, ProfileActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile)

        if (!AppUtils.isUserLoggedIn(this) || AppUtils.isFakeLogin(this)) {
            OnBoardingActivity.restartApp(this, OnBoardingActivityArgs(true))
            return
        }

        tvPrivacyPolicy.setOnClickListener{
            viewModel.handle(ProfileViewActions.PrivacyPolicyClicked)
        }

        viewModel.viewState.observe(this, {
            it?.let { updateState(it) }
        })

        viewModel.viewEvents.observe(this, {
            when (it) {
                is ProfileViewEvents.ShowToast -> {
                    toast(it.text)
                    if (it.finishActivity) {
                        navigateUp()
                    }
                }
                is ProfileViewEvents.ShareText -> shareCode(it)
                ProfileViewEvents.RestartApp -> OnBoardingActivity.restartApp(
                    this, OnBoardingActivityArgs(
                        clearNotification = true
                    )
                )
                is ProfileViewEvents.ShowUpdateServerDialog -> {
                    showUpdateServerDialog(it.serverUrl)
                }
                is ProfileViewEvents.OpenUrl -> {
                    merakiNavigator.openCustomTab(it.url, this)
                }
            }
        })

        mBinding.updateProfile.setOnClickListener {
            viewModel.handle(
                ProfileViewActions.UpdateProfile(
                    etName.text.toString(),
                    etEmail.text.toString()
                )
            )
        }

        mBinding.ivEdit.setOnClickListener {
            viewModel.handle(ProfileViewActions.EditProfileClicked)
        }

        mBinding.rlServerUrl.setOnClickListener {
            viewModel.handle(ProfileViewActions.UpdateServerUrlClicked)
        }

        initSavedFile()
        initToolBar()
    }

    private fun showUpdateServerDialog(serverUrl: String) {
        val inputText = EditText(this)
        val alert = MaterialAlertDialogBuilder(this)
            .setTitle("Server Url")
            .setView(inputText)
            .setPositiveButton("OK") { _, _ ->
                viewModel.handle(ProfileViewActions.UpdateServerUrl(inputText.text.toString()))
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setNeutralButton("Reset") { _, _ ->
                viewModel.handle(ProfileViewActions.ResetServerUrl)
            }
            .create()
        alert.setOnShowListener {
            val margin = resources.getDimensionPixelSize(R.dimen.spacing_4x)
            inputText.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                marginEnd = margin
                marginStart = margin
            }
            inputText.setText(serverUrl)
        }
        alert.show()
    }


    private fun shareCode(it: ProfileViewEvents.ShareText) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, it.text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(
            sendIntent,
            getString(org.navgurukul.playground.R.string.share_code)
        )
        startActivity(shareIntent)
    }

    private fun updateState(it: ProfileViewState) {
        mBinding.tvAppVersion.text = it.appVersionText
        it.userName?.let {
            mBinding.tvName.text = it
            mBinding.etName.setText(it)
            mBinding.etName.setSelection(it.length)
        }

        it.userEmail?.let {
            mBinding.tvEmail.text = it
            mBinding.etEmail.setText(it)
            mBinding.etEmail.setSelection(it.length)
        }

        if (it.savedFiles.isEmpty()) {
            mBinding.rlSavedFile.visibility = View.GONE
        } else {
            mBinding.rlSavedFile.visibility = View.VISIBLE
            val adapter = mBinding.recyclerview.adapter as SavedFileAdapter
            adapter.submitList(it.savedFiles)
        }

        it.showAllButtonText?.let {
            mBinding.tvViewAll.isVisible = true
            mBinding.tvViewAll.text = it
        } ?: run {
            mBinding.tvViewAll.isVisible = false
        }

        mBinding.updateProfile.isVisible = it.showUpdateProfile
        mBinding.llProfileEdit.isVisible = it.showEditProfileLayout
        mBinding.updateProfile.isVisible = it.showUpdateProfile
        mBinding.progressBarButton.isVisible = it.showProgressBar

        //Update profile image only when it changes
        if (mBinding.ivProfile.getTag(R.id.ivProfile) == null ||
            mBinding.ivProfile.getTag(R.id.ivProfile)!= it.profilePic) {
            val requestOptions = RequestOptions()
                .centerCrop()
                .transform(CircleCrop())

            val thumbnail = GlideApp.with(this)
                .load(R.drawable.illus_default_avatar)
                .apply(requestOptions)

            GlideApp.with(mBinding.ivProfile)
                .load(it.profilePic)
                .apply(requestOptions)
                .thumbnail(thumbnail)
                .transform(CircleCrop())
                .into(mBinding.ivProfile)

            mBinding.ivProfile.setTag(R.id.ivProfile, it.profilePic)
        }

        if (it.showServerUrl) {
            mBinding.rlServerUrl.visibility = View.VISIBLE
            mBinding.tvServerUrl.text = it.serverUrl
        } else {
            mBinding.rlServerUrl.visibility = View.GONE
        }
    }

    private fun initSavedFile() {
        val adapter = SavedFileAdapter { file, view ->
            showPopupMenu(file, view)
        }
        val padding = resources.getDimensionPixelSize(R.dimen.spacing_2x)
        mBinding.recyclerview.layoutManager = GridLayoutManager(this, 2)
        mBinding.recyclerview.addItemDecoration(GridSpacingDecorator(padding, padding, 2))
        mBinding.recyclerview.adapter = adapter

        mBinding.tvViewAll.setOnClickListener {
            viewModel.handle(ProfileViewActions.ExpandFileList)
        }
    }

    private fun showPopupMenu(file: File, view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.popup_menu_saved_file, popup.menu)
        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.share -> viewModel.handle(ProfileViewActions.ShareFile(file))
                R.id.delete -> viewModel.handle(ProfileViewActions.DeleteFile(file))
                R.id.copy -> merakiNavigator.openPlaygroundWithFileContent(this, file)
            }
            true
        }
        popup.show()

    }

    private fun initToolBar() {
        mBinding.idHeader.ivBackButton.setOnClickListener {
            navigateUp()
        }

        mBinding.idHeader.ivLogOut.setOnClickListener {
            showLogOutDialog()
        }
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


    private fun showLogOutDialog() {
        AlertDialog.Builder(this).setMessage(getString(R.string.want_log_out))
            .setPositiveButton(
                getString(R.string.okay)
            ) { dialog, _ ->
                viewModel.handle(ProfileViewActions.LogOut)
                dialog.dismiss()
            }.setNegativeButton(
                getString(R.string.cancel)
            ) { dialog, _ ->
                dialog.dismiss()
            }.create().show()
    }
}