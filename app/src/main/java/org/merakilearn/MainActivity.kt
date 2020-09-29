package org.merakilearn

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.merakilearn.util.AppUtils

class MainActivity : AppCompatActivity() {
    companion object {
        fun launch(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
            (context as Activity).finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)
        findViewById<View>(R.id.headerIv).setOnClickListener {
            if (AppUtils.isFakeLogin(this))
                OnBoardingActivity.launchLoginFragment(this)
            else
                ProfileActivity.launch(this)
        }
        setHeaderTitle(getString(R.string.app_name), this)
    }

    //Don't make this method private and don't change name or param value as it is used from other module using reflection
    fun setHeaderTitle(title: String, context: Activity) {
        context.findViewById<TextView>(R.id.headerTitle).text = title
    }
}