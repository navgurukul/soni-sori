package org.merakilearn.ui.home

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.merakilearn.MainActivity
import org.merakilearn.OnBoardingActivity
import org.merakilearn.R
import org.merakilearn.databinding.FragmentUserProfileBinding
import org.merakilearn.datasource.network.model.LoginResponse
import org.merakilearn.ui.onboarding.LoginFragment
import org.merakilearn.ui.onboarding.LoginViewModel
import org.merakilearn.util.AppUtils
import org.navgurukul.learn.ui.common.toast


class ProfileFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileFragment()
        const val TAG = "ProfileFragment"
        private const val RC_SIGN_IN = 9001
    }

    private lateinit var mBinding: FragmentUserProfileBinding
    private val viewModel: LoginViewModel by viewModel()
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var isFakeLogin = false
    private lateinit var user: LoginResponse.User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_user_profile, container, false)
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initLinkButton()
        initGoogleSignInOption()
        initToolBarClickListener()
        user = AppUtils.getCurrentUser(requireContext())
        mBinding.user = user
    }


    private fun initToolBarClickListener() {
        mBinding.idHeader.ivBackButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        mBinding.ivEdit.setOnClickListener {
            if (AppUtils.isFakeLogin(requireContext())) {
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

    private fun showLogOutDialog() {
        AlertDialog.Builder(requireActivity()).setMessage(getString(R.string.want_log_out))
            .setPositiveButton(
                getString(R.string.okay)
            ) { dialog, _ ->
                viewModel.logOut().observe(viewLifecycleOwner, Observer {
                    if (it) {
                        dialog.dismiss()
                        OnBoardingActivity.launch(requireContext())
                    }
                })
            }.setNegativeButton(
                getString(R.string.cancel)
            ) { dialog, _ ->
                dialog.dismiss()
            }.create().show()
    }

    private fun initLinkButton() {
        if (AppUtils.isFakeLogin(requireContext())) {
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
        viewModel.updateProfile(user).observe(viewLifecycleOwner, Observer {
            toggleProgressBarVisibility(View.GONE)
            mBinding.llProfileEdit.visibility = View.GONE
            if (it) {
                toast(getString(R.string.profile_updated_successfully))
                childFragmentManager.popBackStack()
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
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
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
            MainActivity.launch(requireContext())
        } else
            toast(getString(R.string.unable_to_sign))
    }

    private fun toggleProgressBarVisibility(visibiltiy: Int) {
        mBinding.progressBarButton.visibility = visibiltiy
    }
}