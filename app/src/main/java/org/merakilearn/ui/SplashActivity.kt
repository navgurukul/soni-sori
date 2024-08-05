package org.merakilearn.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import org.merakilearn.R
import org.merakilearn.theme.isChristmas
import org.merakilearn.theme.isNewYear
import org.merakilearn.ui.onboarding.OnBoardingActivity
import timber.log.Timber


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultLauncher ->
            if (resultLauncher.resultCode == RESULT_OK) {
            }
        }
    private val appUpdateManager: AppUpdateManager by lazy {
        AppUpdateManagerFactory.create(applicationContext)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity_app)
        setUpTheme()
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, OnBoardingActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)

        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener {
            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                it.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    it,
                    this,
                    AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE)
                        .setAllowAssetPackDeletion(true).build(),
                    UPDATE_REQUEST_CODE
                )
                resultLauncher.launch(intent)
            } else {
                Timber.d("No Update Available")
            }
        }.addOnFailureListener {
            Timber.d("Update Failed: $it")
        }
    }

    private fun setUpTheme() {
        when {
            isChristmas() -> setChristmasTheme()
            isNewYear() -> setNewYearTheme()
            else -> setNormalTheme()
        }
    }

    private fun setChristmasTheme() {
        findViewById<View>(R.id.headerImageViewForChristmas).visibility = View.VISIBLE
        findViewById<View>(R.id.headerImageViewForNewYear).visibility = View.GONE
        findViewById<View>(R.id.snowAnimationView).visibility = View.VISIBLE
        findViewById<View>(R.id.logoImageViewForChristmas).visibility = View.VISIBLE
        findViewById<View>(R.id.logoImageView).visibility = View.GONE
        findViewById<View>(R.id.quoteTextView).visibility = View.VISIBLE
        findViewById<View>(R.id.navgurukulTextImageView).visibility = View.VISIBLE
    }

    private fun setNewYearTheme() {
        findViewById<View>(R.id.headerImageViewForChristmas).visibility = View.GONE
        findViewById<View>(R.id.headerImageViewForNewYear).visibility = View.VISIBLE
        findViewById<View>(R.id.snowAnimationView).visibility = View.GONE
        findViewById<View>(R.id.newYearAnimation).visibility = View.VISIBLE
        findViewById<View>(R.id.logoImageViewForChristmas).visibility = View.GONE
        findViewById<View>(R.id.logoImageView).visibility = View.VISIBLE
        findViewById<View>(R.id.quoteTextView).visibility = View.VISIBLE
        findViewById<View>(R.id.navgurukulTextImageView).visibility = View.VISIBLE
        findViewById<View>(R.id.splashLayout)
            .setBackgroundColor(resources.getColor(R.color.white))
    }

    private fun setNormalTheme() {
        findViewById<View>(R.id.headerImageViewForChristmas).visibility = View.GONE
        findViewById<View>(R.id.headerImageViewForNewYear).visibility = View.GONE
        findViewById<View>(R.id.snowAnimationView).visibility = View.GONE
        findViewById<View>(R.id.logoImageViewForChristmas).visibility = View.GONE
        findViewById<View>(R.id.logoImageView).visibility = View.VISIBLE
        findViewById<View>(R.id.quoteTextView).visibility = View.GONE
        findViewById<View>(R.id.navgurukulTextImageView).visibility = View.GONE
        findViewById<View>(R.id.splashLayout)
            .setBackgroundColor(resources.getColor(R.color.white))
    }
}
const val UPDATE_REQUEST_CODE = 524