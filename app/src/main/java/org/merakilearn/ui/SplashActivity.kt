package org.merakilearn.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import org.merakilearn.R
import org.merakilearn.databinding.SplashActivityAppBinding
import org.merakilearn.theme.isChristmas
import org.merakilearn.theme.isNewYear
import org.merakilearn.ui.onboarding.OnBoardingActivity
import timber.log.Timber
import org.navgurukul.commonui.platform.setupEdgeToEdge


const val UPDATE_REQUEST_CODE = 524
class SplashActivity : AppCompatActivity() {
    private lateinit var binding : SplashActivityAppBinding

    private val navigationHandler = Handler(Looper.getMainLooper())
    private val navigationRunnable = Runnable { navigateToNextScreen() }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultLauncher ->
            if (resultLauncher.resultCode == RESULT_OK) {
            }
        }
    private val appUpdateManager: AppUpdateManager by lazy {
        AppUpdateManagerFactory.create(applicationContext)
    }

    private fun navigateToNextScreen() {
        val intent = Intent(this, OnBoardingActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.splash_activity_app)
        setupEdgeToEdge()
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        setUpTheme()
        navigationHandler.postDelayed(navigationRunnable, 3000)

        appUpdateInfoTask.addOnSuccessListener {
            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && it.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                navigationHandler.removeCallbacks(navigationRunnable)
                try {
                    appUpdateManager.startUpdateFlowForResult(
                        it,
                        this,
                        AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE)
                            .setAllowAssetPackDeletion(true).build(),
                        UPDATE_REQUEST_CODE
                    )
                } catch (e: Exception) {
                    Timber.e(e, "Failed to start update flow")
                    navigateToNextScreen()
                }

            } else {
                //Toast.makeText(this, "No Update Available", Toast.LENGTH_SHORT).show()
                Timber.d("No Update Available")
            }
        }.addOnFailureListener {
            //Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show()
            Timber.d("Update Failed : $it")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UPDATE_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Timber.d("Update flow failed or cancelled by user, code: $resultCode")
            } else {
                Timber.d("Update flow completed/accepted by user")
            }
            navigateToNextScreen()
        }
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