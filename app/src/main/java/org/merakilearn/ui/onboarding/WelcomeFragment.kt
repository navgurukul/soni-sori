package org.merakilearn.ui.onboarding

import android.content.Context
import android.os.Build
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.fragment_welcome.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.merakilearn.R
import org.merakilearn.core.navigator.MerakiNavigator
import org.navgurukul.commonui.platform.BaseFragment
import org.navgurukul.learn.ui.common.toast
import timber.log.Timber


class WelcomeFragment : BaseFragment() {

    companion object {
        fun newInstance() = WelcomeFragment()
        const val TAG = "WelcomeFragment"
        private const val RC_SIGN_IN = 9001
    }

    private val viewModel: WelcomeViewModel by viewModel()

    private val navigator: MerakiNavigator by inject()
    private val googleSignInClient: GoogleSignInClient by lazy {
        GoogleSignIn.getClient(requireActivity(), GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.server_client_id))
            .build())
    }

    override fun getLayoutResId(): Int = R.layout.fragment_welcome

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvSkipLogin.setOnClickListener {
            viewModel.handle(WelcomeViewActions.InitiateFakeSignUp)
        }

        tvConnectWithEmail.setOnClickListener {
            signInWithGoogle()
        }

        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            progress_bar_button.visibility = if (it.isLoading) View.VISIBLE else View.GONE
            it.initialSyncProgress?.let { progressState ->
                showLoading(getString(progressState.statusText))
            } ?: run {
                dismissLoadingDialog()
            }
        })

        viewModel.viewEvents.observe(viewLifecycleOwner, Observer {
            when(it) {
                is WelcomeViewEvents.ShowToast -> toast(it.toastText)
                is WelcomeViewEvents.OpenMerakiChat -> openMerakiChat(it.roomId)
                is WelcomeViewEvents.OpenHomeScreen -> openHomeScreen()
            }
        })
    }

    private fun openHomeScreen() {
        navigator.openHome(requireContext(), true)
        requireActivity().finish()
    }

    private fun signInWithGoogle() {
        googleSignInClient.signOut().addOnCompleteListener {
            startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
        }
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

            account.idToken?.let {
                viewModel.handle(WelcomeViewActions.LoginWithAuthToken(it))
            } ?: run {
                toast(getString(R.string.unable_to_sign))
            }

        } catch (e: ApiException) {
            Timber.e(e, "Google Sign Failed")
            toast(getString(R.string.unable_to_sign))
        }
    }

    private fun openMerakiChat(roomId: String) {
        navigator.openRoom(requireContext(), roomId, true)
        requireActivity().finish()
    }
}