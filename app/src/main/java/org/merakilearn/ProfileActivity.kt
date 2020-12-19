package org.merakilearn

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.merakilearn.core.navigator.MerakiNavigator
import org.merakilearn.databinding.ActivityProfileBinding
import org.merakilearn.datasource.network.model.LoginResponse
import org.merakilearn.ui.adapter.SavedFileAdapter
import org.merakilearn.ui.onboarding.LoginViewModel
import org.merakilearn.util.AppUtils
import org.navgurukul.chat.core.glide.GlideApp
import org.navgurukul.commonui.platform.GridSpacingDecorator
import org.navgurukul.learn.ui.common.toast
import java.io.File


class ProfileActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityProfileBinding
    private val viewModel: LoginViewModel by viewModel()
    private lateinit var user: LoginResponse.User
    private var isFromDeepLink = false
    private val filesList = mutableListOf<Pair<String, String>>()
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
        if (AppUtils.isUserLoggedIn(this) && !AppUtils.isFakeLogin(this)) {
            initIntentFilter()
            initLinkButton()
            initSavedFile()
            AppUtils.getCurrentUser(this)?.let {
                user = it
                initToolBar()
                mBinding.user = user
                mBinding.tvAppVersion.text =
                    getString(R.string.app_version, BuildConfig.VERSION_NAME)
            }?:run {
                OnBoardingActivity.restartApp(this@ProfileActivity, OnBoardingActivityArgs(true))
            }
        } else {
            OnBoardingActivity.restartApp(this, OnBoardingActivityArgs(true))
        }

    }

    private fun initSavedFile() {
        val adapter = SavedFileAdapter {
            showPopupMenu(it)
        }
        val padding = resources.getDimensionPixelSize(R.dimen.spacing_2x)
        mBinding.recyclerview.layoutManager = GridLayoutManager(this, 2)
        mBinding.recyclerview.addItemDecoration(GridSpacingDecorator(padding, padding, 2))
        mBinding.recyclerview.adapter = adapter
        viewModel.fetchSavedFile.observe(this, Observer {
            updateAdapter(it)
        })

        mBinding.tvViewAll.setOnClickListener {
            adapter.submitList(filesList)
        }
    }

    private fun updateAdapter(
        listData: List<Pair<String, String>>
    ) {
        val adapter = mBinding.recyclerview.adapter as SavedFileAdapter
        filesList.clear()
        filesList.addAll(listData)
        if (listData.isEmpty()) {
            mBinding.rlSavedFile.visibility = View.GONE
        } else {
            mBinding.rlSavedFile.visibility = View.VISIBLE
        }
        if (listData.isNotEmpty() && listData.size >= 4)
            adapter.submitList(listData.subList(0, 4))
        else
            adapter.submitList(listData)
    }

    private fun showPopupMenu(it: Triple<String, String, View>) {
        val popup = PopupMenu(this, it.third)
        popup.menuInflater.inflate(R.menu.popup_menu_saved_file, popup.menu)
        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.share -> shareFile(it.first)
                R.id.delete -> deleteSavedFile(it.first)
                R.id.copy -> copyContent(it.first)
            }
            true
        }
        popup.show()

    }

    private fun deleteSavedFile(first: String) {
        viewModel.deleteFile(first)
        viewModel.deleteFile.observe(this, Observer {
            if (it) {
                val newList = filesList.filter { pair ->
                    pair.first != first
                }.toMutableList()
                updateAdapter(newList)
                filesList.clear()
                filesList.addAll(newList)
                toast(getString(R.string.file_deleted))
            }
        })
    }

    private fun copyContent(fileName: String) {
        merakiNavigator.openPlaygroundWithFileContent(this, fileName)
    }

    private fun shareFile(fileName: String) {
        val code = File(fileName).bufferedReader().readLine()
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, code)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(
            sendIntent,
            getString(org.navgurukul.playground.R.string.share_code)
        )
        startActivity(shareIntent)
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
        mBinding.linkAccount.visibility = View.GONE
    }

    private fun updateProfile() {
        toggleProgressBarVisibility(View.VISIBLE)
        viewModel.updateProfile(user).observe(this, Observer {
            toggleProgressBarVisibility(View.GONE)
            mBinding.llProfileEdit.visibility = View.GONE
            mBinding.linkAccount.visibility = View.GONE
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
