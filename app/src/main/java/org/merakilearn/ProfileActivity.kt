package org.merakilearn

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.merakilearn.databinding.ActivityProfileBinding
import org.merakilearn.datasource.network.model.LoginResponse
import org.merakilearn.ui.common.toast
import org.merakilearn.ui.onboarding.LoginFragment
import org.merakilearn.ui.onboarding.LoginViewModel
import org.merakilearn.util.AppUtils

class ProfileActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityProfileBinding
    private val viewModel: LoginViewModel by viewModel()
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var isFakeLogin = false
    private lateinit var user: LoginResponse.User
    private var isFromDeepLink = false

    companion object {
        private const val RC_SIGN_IN = 9001
        fun launch(context: Context) {
            val intent = Intent(context, ProfileActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        AppUtils.validateLoginStatus(this)
        initIntentFilter()
        initLinkButton()
        initGoogleSignInOption()
        initToolBarClickListener()
        user = AppUtils.getCurrentUser(this)
        mBinding.user = user

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

    private fun initToolBarClickListener() {
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
                        OnBoardingActivity.launch(this)
                    }
                })
            }.setNegativeButton(
                getString(R.string.cancel)
            ) { dialog, _ ->
                dialog.dismiss()
            }.create().show()
    }

    private fun initLinkButton() {
        if (AppUtils.isFakeLogin(this)) {
            mBinding.linkAccount.visibility = View.VISIBLE
            isFakeLogin = true
        } else {
            mBinding.linkAccount.text = getString(R.string.save)
        }
        mBinding.linkAccount.setOnClickListener {
            if (isFakeLogin)
                signIn()
            else
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


    private fun initGoogleSignInOption() {
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.server_client_id))
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }


    private fun signIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            getBackendServerToken(account?.idToken)
        } catch (e: ApiException) {
            Log.e(LoginFragment.TAG, "signInResult:failed code=", e)
            toast(getString(R.string.unable_to_sign))
        }
    }

    private fun getBackendServerToken(idToken: String?) {
        toggleProgressBarVisibility(View.VISIBLE)
        viewModel.initLoginServer(idToken).observe(this, Observer {
            toggleProgressBarVisibility(View.GONE)
            proceedWithSignInResult(it)
        })
    }

    private fun proceedWithSignInResult(it: Boolean) {
        if (it) {
            toast(getString(R.string.account_linked_successfully))
            MainActivity.launch(this)
        } else
            toast(getString(R.string.unable_to_sign))
    }

    private fun toggleProgressBarVisibility(visibiltiy: Int) {
        mBinding.progressBarButton.visibility = visibiltiy
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishCurrent()
    }
}