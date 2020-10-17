package org.merakilearn

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.merakilearn.core.appopen.AppOpenDelegate
import org.merakilearn.util.AppUtils

@Parcelize
data class MainActivityArgs(
    val clearNotification: Boolean
) : Parcelable

class MainActivity : AppCompatActivity() {

    companion object {
        const val KEY_ARG = "MainActivity:args"

        fun launch(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
            (context as Activity).finish()
        }

        fun newIntent(context: Context, clearNotification: Boolean = false): Intent {
            val args = MainActivityArgs(
                clearNotification = clearNotification
            )

            return Intent(context, OnBoardingActivity::class.java)
                .apply {
                    putExtra(OnBoardingActivity.KEY_ARG, args)
                }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.getParcelableExtra<MainActivityArgs>(KEY_ARG)?.let { args ->
            appOpenDelegate.onHomeScreenOpened(this, args.clearNotification)
        }
    }


    private val appOpenDelegate: AppOpenDelegate by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        nav_view.setupWithNavController(navHostFragment.navController)

        intent.getParcelableExtra<MainActivityArgs>(KEY_ARG)?.let { args ->
            appOpenDelegate.onHomeScreenOpened(this, args.clearNotification)
        }
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

    fun toggleSearch(visibility: Int): SearchView? {
        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.visibility = visibility
        return searchView
    }
}