package org.merakilearn.ui.onboarding

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.merakilearn.R
import org.merakilearn.databinding.ActivityUsernameLoginBinding

class UsernameLoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityUsernameLoginBinding
    private val viewModel: OnBoardingPagesViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_username_login)


        binding.errorMessagePass.visibility = View.GONE
        binding.errorMessageUserId.visibility = View.GONE

        viewModel.viewEvents.observe(this){
            when(it){
                is OnBoardingPagesEvents.ShowToast -> Toast.makeText(this, it.toastText, Toast.LENGTH_SHORT).show()
            }
        }

        binding.loginButton.setOnClickListener {
            val userId = binding.userIDEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            if (userId.isEmpty() || password.isEmpty()) {
                if (userId.isEmpty()) {
                    binding.errorMessageUserId.visibility = View.VISIBLE
                } else {
                    binding.errorMessageUserId.visibility = View.GONE
                }
                if (password.isEmpty()) {
                    binding.errorMessagePass.visibility = View.VISIBLE
                } else {
                    binding.errorMessagePass.visibility = View.GONE
                }
            } else {
                Toast.makeText(this, "Data is filled properly", Toast.LENGTH_SHORT).show()
            }
            viewModel.loginWithUsername(userId, password)
            Log.d("Username", "Login Button Clicked ${userId}")

        }

        binding.backArrow.setOnClickListener {
            this.onBackPressed()
        }

    }
}