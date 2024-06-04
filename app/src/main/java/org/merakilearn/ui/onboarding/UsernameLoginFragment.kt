package org.merakilearn.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
        binding= FragmentUsernameLoginBinding.inflate(inflater, container, false)
        return binding.root
    }
    private val viewModel: OnBoardingPagesViewModel by viewModel()
    private val onBoardingViewModel: OnBoardingViewModel by sharedViewModel()

    override fun getLayoutResId(): Int = R.layout.fragment_username_login

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.errorMessagePass.visibility = View.GONE
        binding.errorMessageUserId.visibility = View.GONE

        viewModel.viewEvents.observe(viewLifecycleOwner){
            when(it){
                is OnBoardingPagesEvents.ShowToast -> Toast.makeText(requireContext(), it.toastText, Toast.LENGTH_SHORT).show()

            }
        }




        binding.loginButton.setOnClickListener {
            val userId = binding.userIDEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if(userId.isEmpty() && password.isEmpty()){
                Toast.makeText(requireContext(), "Please enter a username and password.", Toast.LENGTH_SHORT).show()
//                binding.textInputLayout.boxStrokeColor = ContextCompat.getColor(requireContext(), R.color.colorRed)
//                binding.textInputLayout2.boxStrokeColor = ContextCompat.getColor(requireContext(), R.color.colorRed)

                binding.errorMessagePass.visibility = View.VISIBLE
                binding.errorMessageUserId.visibility = View.VISIBLE
                return@setOnClickListener
            }else{
                binding.errorMessagePass.visibility = View.GONE
                binding.errorMessageUserId.visibility = View.GONE
            }
6
            onBoardingViewModel.loginWithUsername(userId, password)
        }

        binding.backArrow.setOnClickListener {
//            requireActivity().onBackPressed()
    }


}}