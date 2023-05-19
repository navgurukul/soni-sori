package org.merakilearn.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.merakilearn.R
import org.merakilearn.databinding.OnBoardPagesFragmentBinding
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
    private lateinit var mBinding : OnBoardPagesFragmentBinding

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.on_board_pages_fragment, container, false)
        return mBinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.skipLogin.setOnClickListener {
            viewModel.handle(OnBoardingPagesAction.InitiateFakeSignUp)
        }
        mBinding.loginWithGoogle.setOnClickListener {
            signInWithGoogle()
        }

        viewModel.viewState.observe(viewLifecycleOwner) {
            if (it.isLoading) showLoading() else dismissLoadingDialog()

            if (it.onBoardingData != null && it.onBoardingTranslations != null) {
                configurePages(it.onBoardingData, it.onBoardingTranslations)
            }

            mBinding.navLayout.isVisible = it.isNavLayoutVisible
            mBinding.loginLayout.isVisible = it.isLoginLayoutVisible
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
                is OnBoardingPagesEvents.NavigateToItem -> mBinding.viewPager2.currentItem = it.item
            }
        }
    }

    private fun configurePages(
        onBoardingData: OnBoardingData,
        onBoardingTranslations: OnBoardingTranslations
    ) {
        mBinding.apply {
        next.text = onBoardingTranslations.nextText
        skip.text = onBoardingTranslations.skipText
        loginWithGoogle.text = onBoardingTranslations.loginWithGoogleText
        skipLogin.text = onBoardingTranslations.skipLoginText

        if (viewPager2.adapter == null) {
            val onBoardPagesAdapter =
                OnBoardPagesAdapter(
                    onBoardingData,
                    onBoardingTranslations,
                    requireContext()
                )

            viewPager2.adapter = onBoardPagesAdapter
            TabLayoutMediator(tabLayout, viewPager2) { _, _ -> }.attach()
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