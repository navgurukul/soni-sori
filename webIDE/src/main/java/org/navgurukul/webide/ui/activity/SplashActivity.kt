package org.navgurukul.webide.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import org.merakilearn.core.extentions.toBundle
import org.merakilearn.core.navigator.Mode
import org.navgurukul.webIDE.R
import org.navgurukul.webIDE.databinding.ActivitySplashBinding
import org.navgurukul.webide.extensions.*
import org.navgurukul.webide.util.Prefs.defaultPrefs
import org.navgurukul.webide.util.Prefs.get
import org.navgurukul.webide.util.ui.FontsOverride

class SplashActivity : ThemedActivity() {

    private lateinit var binding : ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        FontsOverride.setDefaultFont(applicationContext,
                "MONOSPACE", "fonts/Inconsolata-Regular.ttf")

        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.hyperLogo.animate().alpha(1F).setDuration(1000).onAnimationStop {
            setupPermissions()
        }
    }

    private fun startIntro() {
        val prefs = defaultPrefs(this)
        val classTo = if (prefs["intro_done", false]!!) {
            MainActivity::class.java
        } else {
            IntroActivity::class.java
        }

        startAndFinish(Intent(this, classTo).withFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
    }

    private fun showPermissionSnack() {
        binding.splashLayout.snack(R.string.permission_storage_rationale, Snackbar.LENGTH_INDEFINITE) {
            action("GRANT") {
                dismiss()
                startActivityForResult(Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", packageName, null)
                }, WRITE_PERMISSION_REQUEST)
            }
        }
    }

    private fun setupPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showPermissionSnack()
            } else {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        WRITE_PERMISSION_REQUEST)
            }
        } else {
            startIntro()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == WRITE_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startIntro()
            }
        } else {
            showPermissionSnack()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == WRITE_PERMISSION_REQUEST) {
            setupPermissions()
        }
    }

    companion object {
            fun newIntent(context: Context, mode: Mode, retake: Boolean = false): Intent {
                return Intent(context, SplashActivity::class.java).apply {
                    putExtras(MainActivityArgs(mode, retake).toBundle()!!)
                }
        }

        private const val WRITE_PERMISSION_REQUEST = 0
    }
}
