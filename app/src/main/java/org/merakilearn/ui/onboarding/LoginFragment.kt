package org.merakilearn.ui.onboarding

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
import org.merakilearn.R
import org.merakilearn.databinding.FragmentLoginBinding
import org.navgurukul.learn.ui.common.toast


class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
        const val TAG = "LoginFragment"
        private const val RC_SIGN_IN = 9001
        private const val LINK_ACCOUNT = "arg_link"
    }

    private lateinit var mBinding: FragmentLoginBinding
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private val viewModel: LoginViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        return mBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                it.window.statusBarColor = ContextCompat.getColor(it, R.color.colorWhite)
            } else {
                it.window.statusBarColor = ContextCompat.getColor(it, R.color.primaryDarkColor)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initGoogleSignInOption()
        mBinding.tvlogin.setOnClickListener {
            showConfirmation()
        }

        mBinding.skipLogin.setOnClickListener {
            MainActivity.launch(requireContext())
        }
    }

    private fun showConfirmation() {
        AlertDialog.Builder(requireActivity()).setMessage(getString(R.string.want_login))
            .setPositiveButton(
                getString(R.string.okay)
            ) { dialog, _ ->
                dialog.dismiss()
                logOutAndSignInAgain()
            }.setNegativeButton(
                getString(R.string.cancel)
            ) { dialog, _ ->
                dialog.dismiss()
            }.create().show()
    }

    private fun logOutAndSignInAgain() {
        mGoogleSignInClient?.signOut()?.addOnCompleteListener {
            viewModel.logOut().observe(viewLifecycleOwner, Observer {
                if (it)
                    signIn()

            })
        }
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
            logOutSelectedAccount(account)
        } catch (e: ApiException) {
            Log.e(TAG, "signInResult:failed code=", e)
            toast(getString(R.string.unable_to_sign))
        }
    }

    private fun logOutSelectedAccount(account: GoogleSignInAccount) {
        mGoogleSignInClient?.signOut()?.addOnSuccessListener {
            getBackendServerToken(account.idToken)
        }
    }

    private fun getBackendServerToken(idToken: String?) {
        toggleProgressBarVisibility(View.VISIBLE)
        viewModel.initLoginServer(idToken).observe(this, Observer {
            toggleProgressBarVisibility(View.GONE)
            if (it != null) {
                MainActivity.launch(requireContext())
            } else
                toast(getString(R.string.email_already_used))
        })
    }

    private fun toggleProgressBarVisibility(visibiltiy: Int) {
        mBinding.progressBarButton.visibility = visibiltiy
    }

}
