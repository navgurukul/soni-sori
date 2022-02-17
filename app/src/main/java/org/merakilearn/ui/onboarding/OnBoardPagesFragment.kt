package org.merakilearn.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.on_board_pages_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.merakilearn.R
import org.merakilearn.datasource.network.model.OnBoardingData
import org.merakilearn.datasource.network.model.OnBoardingTranslations
import org.navgurukul.commonui.platform.BaseFragment
import org.navgurukul.learn.ui.common.toast

class OnBoardPagesFragment : BaseFragment() {

    companion object {
        fun newInstance() = OnBoardPagesFragment()
        val TAG = OnBoardPagesFragment::class.java.name
        private const val RC_SIGN_IN = 9001
    }

    private val viewModel: OnBoardingPagesViewModel by viewModel()
    private val onBoardingViewModel: OnBoardingViewModel by sharedViewModel()

    private val googleSignInClient: GoogleSignInClient by lazy {
        GoogleSignIn.getClient(
            requireActivity(), GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
            )
                .requestEmail()
                .requestIdToken(getString(R.string.server_client_id))
                .build()
        )
    }

    override fun getLayoutResId(): Int = R.layout.on_board_pages_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        skip_login.setOnClickListener {
            viewModel.handle(OnBoardingPagesAction.InitiateFakeSignUp)
        }
        login_with_google.setOnClickListener {
            signInWithGoogle()
        }

        viewModel.viewState.observe(viewLifecycleOwner) {
            if (it.isLoading) showLoading() else dismissLoadingDialog()

            if (it.onBoardingData != null && it.onBoardingTranslations != null) {
                configurePages(it.onBoardingData, it.onBoardingTranslations)
            }

            nav_layout.isVisible = it.isNavLayoutVisible
            login_layout.isVisible = it.isLoginLayoutVisible
        }

        viewModel.viewEvents.observe(viewLifecycleOwner) {
            when (it) {
                is OnBoardingPagesEvents.ShowToast -> toast(it.toastText)
                is OnBoardingPagesEvents.OpenHomePage -> onBoardingViewModel.handle(
                    OnBoardingViewActions.OpenHomeScreen(it.id)
                )
                is OnBoardingPagesEvents.OpenCourseSelection -> onBoardingViewModel.handle(
                    OnBoardingViewActions.NavigateNextFromOnBoardingScreen
                )
                is OnBoardingPagesEvents.NavigateToItem -> viewPager2.currentItem = it.item
            }
        }
    }

    private fun configurePages(
        onBoardingData: OnBoardingData,
        onBoardingTranslations: OnBoardingTranslations
    ) {
        next.text = onBoardingTranslations.nextText
        skip.text = onBoardingTranslations.skipText
        login_with_google.text = onBoardingTranslations.loginWithGoogleText
        skip_login.text = onBoardingTranslations.skipLoginText

        if (viewPager2.adapter == null) {
            val onBoardPagesAdapter =
                OnBoardPagesAdapter(
                    onBoardingData,
                    onBoardingTranslations,
                    requireContext()
                )

            viewPager2.adapter = onBoardPagesAdapter
            TabLayoutMediator(tab_layout, viewPager2) { _, _ -> }.attach()
            (viewPager2.getChildAt(0) as RecyclerView).overScrollMode =
                RecyclerView.OVER_SCROLL_NEVER

            viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    viewModel.handle(OnBoardingPagesAction.PageSelected(position))
                }
            })

            //This is needed to properly center items in viewpage2
            viewPager2.currentItem = 1
            viewPager2.currentItem = 0

            skip.setOnClickListener {
                viewModel.handle(OnBoardingPagesAction.Skip(onBoardPagesAdapter.itemCount))
            }

            next.setOnClickListener {
                viewModel.handle(OnBoardingPagesAction.Next(viewPager2.currentItem))
            }
        }

    }

    private fun signInWithGoogle() {
        googleSignInClient.signOut().addOnCompleteListener {
            startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task?.getResult(ApiException::class.java)
                account?.idToken?.let {
                    viewModel.handle(OnBoardingPagesAction.LoginWithAuthToken(it))
                } ?: run {
                    toast(getString(R.string.unable_to_sign))
                }
            } catch (ex: Exception) {
                FirebaseCrashlytics.getInstance().recordException(ex)
                toast(getString(R.string.unable_to_sign))
            }
        }
    }
}