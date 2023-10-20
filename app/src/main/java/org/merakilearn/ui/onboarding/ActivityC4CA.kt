package org.merakilearn.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.merakilearn.R
import org.merakilearn.databinding.ActivityC4caBinding


class ActivityC4CA : AppCompatActivity() {

    private lateinit var binding: ActivityC4caBinding
    private val viewModel: OnBoardingPagesViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_c4ca)

        binding.loginButton.isEnabled = false
        binding.errorMessageText.visibility = View.GONE

        // Create an OnFocusChangeListener for the username EditText
        binding.userIDEditText.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            binding.loginButton.isEnabled = hasFocus
        }

        viewModel.viewEvents.observe(this){

            when (it){
                is OnBoardingPagesEvents.OpenC4CAHomePage -> {
                    binding.errorMessageText.visibility = View.GONE
                    startActivity(Intent(this, ModuleActtivity::class.java))
                }
                is OnBoardingPagesEvents.ShowToast -> {
                    Toast.makeText(this, it.toastText, Toast.LENGTH_LONG).show()
                }
                is OnBoardingPagesEvents.ShowErrorMessage -> {
                    binding.errorMessageText.visibility = View.VISIBLE
                }
            }
        }

        binding.loginButton.setOnClickListener {
            val username = binding.userIDEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            if(username.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Please enter a username and password.", Toast.LENGTH_SHORT).show();
                return@setOnClickListener
            }

            viewModel.loginC4CA(username, password) //"avengers","Avengers_ln3s"
//            startActivity(Intent(this, ModuleActtivity::class.java))
        }

        binding.backArrow.setOnClickListener {
            onBackPressed()
        }

    }


}