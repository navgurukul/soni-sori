package org.merakilearn.ui.onboarding

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.merakilearn.R
import org.merakilearn.databinding.FragmentUsernameLoginBinding
import org.navgurukul.commonui.platform.BaseFragment

class UsernameLoginFragment : BaseFragment() {

    private lateinit var binding: FragmentUsernameLoginBinding

    companion object {
        fun newInstance() = UsernameLoginFragment()
        val TAG = UsernameLoginFragment::class.java.name
        private const val RC_SIGN_IN = 9001
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUsernameLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val viewModel: OnBoardingPagesViewModel by viewModel()
    private val onBoardingViewModel: OnBoardingViewModel by sharedViewModel()

    override fun getLayoutResId(): Int = R.layout.fragment_username_login

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emptyBorderColor = ContextCompat.getColor(requireContext(), R.color.error)
        val emptyHintColor = ContextCompat.getColor(requireContext(), R.color.error)


        binding.apply {
            errorMessagePass.visibility = View.GONE
            errorMessageUserId.visibility = View.GONE
            mainErrorMsg.visibility = View.GONE
        }

        onBoardingViewModel.viewEvents.observe(viewLifecycleOwner) {
            when (it) {
                is OnBoardingViewEvents.ShowToast -> Toast.makeText(
                    requireContext(),
                    it.toastText,
                    Toast.LENGTH_SHORT
                ).show()

                is OnBoardingViewEvents.ShowErrorMessage -> {
                    binding.mainErrorMsg.visibility = View.VISIBLE
                }

                is OnBoardingViewEvents.ShowUseIdErrorMessage -> {
                    binding.apply {
                        mainErrorMsg.visibility = View.GONE
                        textInputLayout.requestFocus()
                        textInputLayout.apply {
                            setBoxStrokeColorStateList(ColorStateList.valueOf(emptyBorderColor))
                            hintTextColor = ColorStateList.valueOf(emptyHintColor)
                        }
                        showErrorMessages(
                            showUserIdError = true,
                            showPassError = false,
                            message = it.message
                        )
                    }
                }

                is OnBoardingViewEvents.ShowUserPassError -> {
                    binding.apply {
                        mainErrorMsg.visibility = View.GONE
                        textInputLayout2.apply {
                            setBoxStrokeColorStateList(ColorStateList.valueOf(emptyBorderColor))
                            hintTextColor = ColorStateList.valueOf(emptyHintColor)
                        }
                        textInputLayout2.requestFocus()
                        showErrorMessages(
                            showUserIdError = false,
                            showPassError = true,
                            message = it.message
                        )

                    }
                }
            }
        }

        binding.apply {
            userIDEditText.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
                binding.errorMessagePass.visibility = View.GONE
                binding.errorMessageUserId.visibility = View.GONE
                binding.mainErrorMsg.visibility = View.GONE

            }
            passwordEditText.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
                binding.errorMessagePass.visibility = View.GONE
                binding.errorMessageUserId.visibility = View.GONE
                binding.mainErrorMsg.visibility = View.GONE
            }
        }



        binding.loginButton.setOnClickListener {
            val userId = binding.userIDEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (userId.isEmpty() && password.isEmpty()) {
                binding.apply {
                    errorMessagePass.visibility = View.VISIBLE
                    errorMessageUserId.visibility = View.VISIBLE
                }

            } else if (userId.isEmpty()) {
                showErrorMessages(
                    showUserIdError = true,
                    showPassError = false,
                    message = "Please enter a username."
                )
                binding.errorMessageUserId.visibility = View.VISIBLE
                binding.errorMessagePass.visibility = View.GONE
            } else if (password.isEmpty()) {
                showErrorMessages(
                    showUserIdError = false,
                    showPassError = true,
                    message = "Please enter a password."
                )
                binding.errorMessageUserId.visibility = View.GONE
                binding.errorMessagePass.visibility = View.VISIBLE
            } else {
                onBoardingViewModel.loginWithUsername(userId, password)
            }

        }

        binding.backArrow.setOnClickListener {
            onBoardingViewModel.handle(OnBoardingViewActions.BackToOnboardingPages)
        }


    }

    private fun showErrorMessages(
        showUserIdError: Boolean,
        showPassError: Boolean,
        message: String
    ) {
        binding.errorMessageUserId.apply {
            visibility = if (showUserIdError) View.VISIBLE else View.GONE
            text = message
        }

        binding.errorMessagePass.apply {
            visibility = if (showPassError) View.VISIBLE else View.GONE
            text = message
        }
    }

}