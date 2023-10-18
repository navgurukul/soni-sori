package org.merakilearn.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import kotlinx.android.synthetic.main.on_board_pages_fragment.login_with_c4ca
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.merakilearn.R
import org.merakilearn.databinding.ActivityC4caBinding
import org.navgurukul.learn.ui.learn.LearnFragment


class ActivityC4CA : AppCompatActivity() {

    private lateinit var binding: ActivityC4caBinding
    private val viewModel: OnBoardingPagesViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_c4ca)
        binding.loginButton.setOnClickListener {
            val username = binding.userIDEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            if(username.isEmpty() || password.isEmpty()){
                return@setOnClickListener
            }
           viewModel.loginC4CA(username, password) //"avengers","Avengers_ln3s"

            viewModel.login.observe(this, {
                Log.d("ActivityC4CA", "onCreate: $it")
                startActivity(Intent(this, LearnFragment::class.java))
            })

            //startActivity(Intent(this,LearnFragment::class.java))
            //signInWithC4ca()
        }
    }
//    private fun signInWithC4ca() {
//        viewModel.loginC4CA("Avengers_ln3s")
//        Log.d(OnBoardPagesFragment.TAG, "signInWithC4ca: ")
//    }
}