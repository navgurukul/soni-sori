package org.merakilearn.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.databinding.DataBindingUtil
import org.merakilearn.R
import org.merakilearn.databinding.ActivitySplashBinding
import org.merakilearn.theme.isChristmas
import org.merakilearn.theme.isNewYear
import org.merakilearn.ui.onboarding.OnBoardingActivity



class SplashActivity : AppCompatActivity() {
   private lateinit var binding : ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_splash)
        setUpTheme()
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, OnBoardingActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }

    private fun setUpTheme(){
        when {
            isChristmas() -> {
                setChristmasTheme()
            }
            isNewYear() -> {
                setNewYearTheme()
            }
            else -> {
                setNormalTheme()
            }
        }
    }

    private fun setChristmasTheme(){
        binding.apply{
            headerImageViewForChristmas.visibility = View.VISIBLE
            headerImageViewForNewYear.visibility = View.GONE
            snowAnimationView.visibility = View.VISIBLE
            logoImageViewForChristmas.visibility = View.VISIBLE
            logoImageView.visibility = View.GONE
            quoteTextView.visibility = View.VISIBLE
            navgurukulTextImageView.visibility = View.VISIBLE
        }
    }

    private fun setNewYearTheme(){
        binding.apply{
            headerImageViewForChristmas.visibility = View.GONE
            headerImageViewForNewYear.visibility = View.VISIBLE
            snowAnimationView.visibility = View.GONE
            newYearAnimation.visibility = View.VISIBLE
            logoImageViewForChristmas.visibility = View.GONE
            logoImageView.visibility = View.VISIBLE
            quoteTextView.visibility = View.VISIBLE
            navgurukulTextImageView.visibility = View.VISIBLE
            splashLayout.setBackgroundColor(resources.getColor(R.color.white))
        }
    }

    private fun setNormalTheme(){
        binding.apply{
            headerImageViewForChristmas.visibility = View.GONE
            headerImageViewForNewYear.visibility = View.GONE
            snowAnimationView.visibility = View.GONE
            logoImageViewForChristmas.visibility = View.GONE
            logoImageView.visibility = View.VISIBLE
            quoteTextView.visibility = View.GONE
            navgurukulTextImageView.visibility = View.GONE
            splashLayout.setBackgroundColor(resources.getColor(R.color.white))
        }
    }

}