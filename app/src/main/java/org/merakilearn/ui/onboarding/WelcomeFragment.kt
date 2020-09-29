package org.merakilearn.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_welcome.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.merakilearn.R
import org.merakilearn.util.AppUtils
import org.navgurukul.chat.features.navigator.ChatNavigator
import org.navgurukul.commonui.platform.BaseFragment
import org.navgurukul.learn.ui.common.toast


class WelcomeFragment : BaseFragment() {

    companion object {
        fun newInstance() = WelcomeFragment()
        const val TAG = "WelcomeFragment"
    }

    private val viewModel: WelcomeViewModel by viewModel()

    private val navigator: ChatNavigator by inject()

    override fun getLayoutResId(): Int = R.layout.fragment_welcome

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvAlready.setOnClickListener {
            AppUtils.changeFragment(
                parentFragmentManager,
                LoginFragment.newInstance(),
                R.id.fragment_container_onboarding,
                true,
                LoginFragment.TAG
            )
        }

        tvStarted.setOnClickListener {
            viewModel.handle(WelcomeViewActions.InitiateFakeSignUp)
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
            }
        })
    }

    private fun openMerakiChat(roomId: String) {
        navigator.openRoom(requireContext(), roomId)
        requireActivity().finish()
    }
}