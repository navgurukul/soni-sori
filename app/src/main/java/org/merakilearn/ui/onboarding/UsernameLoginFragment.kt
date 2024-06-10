package org.merakilearn.ui.onboarding

import android.content.Intent
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

        val emptyBorderColor = ContextCompat.getColor(requireContext(), R.color.colorRed)
        val emptyHintColor = ContextCompat.getColor(requireContext(), R.color.colorRed)

        binding.errorMessagePass.visibility = View.GONE
        binding.errorMessageUserId.visibility = View.GONE

        binding.apply {
            userIDEditText.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
                binding.errorMessagePass.visibility = View.GONE
                binding.errorMessageUserId.visibility = View.GONE
            }
            passwordEditText.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
                binding.errorMessagePass.visibility = View.GONE
                binding.errorMessageUserId.visibility = View.GONE
            }
        }

        onBoardingViewModel.viewEvents.observe(viewLifecycleOwner) {
            when (it) {
                is OnBoardingViewEvents.ShowToast -> Toast.makeText(requireContext(), it.toastText, Toast.LENGTH_SHORT).show()
                is OnBoardingViewEvents.ShowErrorMessage -> {
                    binding.errorMessagePass.visibility = View.VISIBLE
                    binding.errorMessageUserId.visibility = View.VISIBLE
                    binding.errorMessagePass.text = "Your username is wrong"
                }
            }
        }

        binding.loginButton.setOnClickListener {
            val userId = binding.userIDEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (userId.isEmpty() && password.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a username and password.", Toast.LENGTH_SHORT).show()
                binding.textInputLayout.apply {
                    setBoxStrokeColorStateList(ColorStateList.valueOf(emptyBorderColor))
                    defaultHintTextColor = ColorStateList.valueOf(emptyHintColor)
                }
                binding.textInputLayout2.apply {
                    setBoxStrokeColorStateList(ColorStateList.valueOf(emptyBorderColor))
                    defaultHintTextColor = ColorStateList.valueOf(emptyHintColor)
                }
                binding.textInputLayout.requestFocus()
                binding.textInputLayout2.requestFocus()
                binding.errorMessagePass.visibility = View.VISIBLE
                binding.errorMessageUserId.visibility = View.VISIBLE
            } else if (userId.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a username.", Toast.LENGTH_SHORT).show()
                showErrorMessages(true, false, "Please enter a username.")
            } else if (password.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a password.", Toast.LENGTH_SHORT).show()
                showErrorMessages(false, true, "Please enter a password.")
            } else {
                binding.errorMessagePass.visibility = View.GONE
                binding.errorMessageUserId.visibility = View.GONE
            }

            onBoardingViewModel.loginWithUsername(userId, password)
        }

        binding.backArrow.setOnClickListener {
            val intent = Intent(requireContext(), OnBoardingActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().finish() // Optional: if you want to finish the current activity
        }
    }

    private fun showErrorMessages(showUserIdError: Boolean, showPassError: Boolean, message: String? = null) {
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
