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
import org.merakilearn.ui.onboarding.OnBoardingActivity
import java.util.*


class SplashActivity : AppCompatActivity() {
   private lateinit var binding : ActivitySplashBinding
   private val currentDate =  Calendar.getInstance()
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
        if (isChristmas()){
            setChristmasTheme()
        }else if (isNewYear()){
            setNewYearTheme()
        }else{
            setNormalTheme()
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
            logoImageViewForChristmas.visibility = View.GONE
            logoImageView.visibility = View.VISIBLE
            quoteTextView.visibility = View.VISIBLE
            navgurukulTextImageView.visibility = View.VISIBLE
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
        }
    }


    private fun isChristmas(): Boolean {
        val christmasStart = Calendar.getInstance().apply {
            set(Calendar.MONTH, Calendar.DECEMBER)
            set(Calendar.DAY_OF_MONTH, 12)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        val christmasEnd = Calendar.getInstance().apply {
            set(Calendar.MONTH, Calendar.DECEMBER)
            set(Calendar.DAY_OF_MONTH, 15)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        return currentDate.after(christmasStart) && currentDate.before(christmasEnd)
    }

    private fun isNewYear(): Boolean {
        val newYearStart = Calendar.getInstance().apply {
            set(Calendar.MONTH, Calendar.DECEMBER)
            set(Calendar.DAY_OF_MONTH, 16)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        val newYearEnd = Calendar.getInstance().apply {
            set(Calendar.MONTH, Calendar.DECEMBER)
            set(Calendar.DAY_OF_MONTH, 20)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        return currentDate.after(newYearStart) && currentDate.before(newYearEnd)
    }
}