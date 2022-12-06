package org.merakilearn.ui.profile

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
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
import org.merakilearn.R
import org.merakilearn.core.navigator.MerakiNavigator
import org.merakilearn.databinding.ActivityProfileBinding
import org.merakilearn.datasource.UserRepo
import org.merakilearn.ui.adapter.SavedFileAdapter
import org.merakilearn.ui.onboarding.OnBoardingActivity
import org.navgurukul.chat.core.glide.GlideApp
import org.navgurukul.commonui.platform.GridSpacingDecorator
import org.navgurukul.learn.ui.common.toast
import java.io.File


class ProfileActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityProfileBinding
    private val viewModel: ProfileViewModel by viewModel()
    private val merakiNavigator: MerakiNavigator by inject()
    private val userRepo: UserRepo by inject()

    companion object {
        fun launch(context: Context) {
            val intent = Intent(context, ProfileActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile)

        if (!userRepo.isUserLoggedIn() || userRepo.isFakeLogin()) {
            OnBoardingActivity.restartApp(this, true)
            return
        }

        btnPrivacyPolicy.setOnClickListener {
            viewModel.handle(ProfileViewActions.PrivacyPolicyClicked)
        }

        viewModel.viewState.observe(this) {
            it?.let { updateState(it) }
        }

        viewModel.viewEvents.observe(this) {
            when (it) {
                is ProfileViewEvents.ShowToast -> {
                    toast(it.text)
                    if (it.finishActivity) {
                        navigateUp()
                    }
                }
                is ProfileViewEvents.ShowUpdateServerDialog -> {
                    showUpdateServerDialog(it.serverUrl)
                }
                is ProfileViewEvents.ShareText -> shareCode(it)
                ProfileViewEvents.RestartApp -> OnBoardingActivity.restartApp(
                    this, clearNotification = true
                )
                is ProfileViewEvents.OpenUrl -> {
                    merakiNavigator.openCustomTab(it.url, this)
                }
            }
        }

        explore_opportunity.setOnClickListener{
            viewModel.handle(ProfileViewActions.ExploreOpportunityClicked)
        }

        mBinding.serverUrlValue.setOnClickListener {
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
//        mBinding.appVersionValue.text = it.appVersionText

        it.userName?.let {
            mBinding.tvName.setText(it)
            mBinding.tvName.setSelection(it.length)
        }

        it.userEmail?.let {
            mBinding.tvEmail.setText(it)
        }

        if (it.savedFiles.isEmpty()) {
            mBinding.tvSavedFile.visibility = View.GONE
        } else {
            mBinding.tvSavedFile.visibility = View.VISIBLE
            val adapter = mBinding.recyclerview.adapter as SavedFileAdapter
            adapter.submitList(it.savedFiles)
        }

        it.showAllButtonText?.let {
            mBinding.tvViewAll.isVisible = true
            mBinding.tvViewAll.text = it
        } ?: run {
            mBinding.tvViewAll.isVisible = false
        }

        if (it.showEditProfileLayout) {
            mBinding.tvEmail.isFocusable = true
            mBinding.tvEmail.isFocusableInTouchMode = true
            mBinding.tvName.setBackgroundResource(R.drawable.bg_profile_edit_text)
            mBinding.tvName.isFocusable = true
            mBinding.tvName.isFocusableInTouchMode = true
            mBinding.tvName.requestFocus()
            mBinding.btnEdit.text = getString(R.string.save)
            mBinding.btnEdit.setOnClickListener {
                viewModel.handle(
                    ProfileViewActions.UpdateProfile(
                        mBinding.tvName.text.toString(),
                        mBinding.tvEmail.text.toString()
                    )
                )
            }
        } else {
            mBinding.tvEmail.background = null
            mBinding.tvEmail.isFocusable = false
            mBinding.tvName.background = null
            mBinding.tvName.isFocusable = false
            mBinding.btnEdit.text = getString(R.string.edit)
            mBinding.btnEdit.setOnClickListener {
                viewModel.handle(ProfileViewActions.EditProfileClicked)
            }
        }

        mBinding.progressBarButton.isVisible = it.showProgressBar

        //Update profile image only when it changes
        if (mBinding.ivProfile.getTag(R.id.ivProfile) == null ||
            mBinding.ivProfile.getTag(R.id.ivProfile) != it.profilePic
        ) {
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
            mBinding.groupServerUrl.visibility = View.VISIBLE
            mBinding.serverUrlValue.text = it.serverUrl
        } else {
            mBinding.groupServerUrl.visibility = View.GONE
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