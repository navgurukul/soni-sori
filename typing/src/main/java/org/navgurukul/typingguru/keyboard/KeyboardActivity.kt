package org.navgurukul.typingguru.keyboard

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.os.Parcelable
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.widget.Toast
import android.graphics.drawable.ColorDrawable
import android.graphics.Color
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_keyboard.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.merakilearn.core.extentions.activityArgs
import org.merakilearn.core.extentions.toBundle
import org.merakilearn.core.navigator.Mode

import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import org.navgurukul.commonui.platform.BaseActivity
import org.navgurukul.typingguru.R
import org.navgurukul.typingguru.score.ScoreActivity
import org.navgurukul.typingguru.score.ScoreActivityArgs
import org.navgurukul.typingguru.webview.WebViewActivity
import org.merakilearn.core.extentions.setWidthPercent

@Parcelize
data class KeyboardActivityArgs(
    val mode: Mode,
    val retake: Boolean = false
) : Parcelable

class KeyboardActivity : BaseActivity() {

    companion object {

        fun newIntent(context: Context, mode: Mode, retake: Boolean = false): Intent {
            return Intent(context, KeyboardActivity::class.java).apply {
                putExtras(KeyboardActivityArgs(mode, retake).toBundle()!!)
            }
        }
    }

    private val keyboardActivityArgs: KeyboardActivityArgs by activityArgs()
    private val viewModel: KeyboardViewModel by viewModel(parameters = {
        parametersOf(
            keyboardActivityArgs
        )
    })

    private var incorrectKeyJob: Job? = null

    private val audioManager: AudioManager? by lazy { getSystemService(Context.AUDIO_SERVICE) as? AudioManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keyboard)

        hideSystemUI()

        viewModel.viewState.observe(this, { state ->
            course_keys_view.setKeys(state.courseKeys)
            course_keys_view.currentKeyIndex = state.activeKeyIndex
            keyboard_view.activeKey = state.activeKeyIndex?.let { state.courseKeys[it].label }
            progressBar.max = state.maxProgress
            progressBar.progress = state.currentProgress
            txt_timer.text = state.timerText
        })

        viewModel.viewEvents.observe(this, {
            when (it) {
                is KeyboardViewEvent.ShakeKey -> {
                    course_keys_view.shakeCurrentKey()
                    audioManager?.playSoundEffect(AudioManager.FX_KEYPRESS_INVALID)
                    keyboard_view.incorrectKey = it.key
                    incorrectKeyJob?.cancel()
                    incorrectKeyJob = lifecycleScope.launch {
                        delay(500)
                        keyboard_view.incorrectKey = null
                    }
                }
                is KeyboardViewEvent.OpenScoreActivity -> {
                    Toast.makeText(this, "Lesson completed", Toast.LENGTH_SHORT).show()
                    intent = ScoreActivity.newInstance(
                        this,
                        ScoreActivityArgs(
                            it.rightKeys,
                            it.wrongKeys,
                            it.timeTaken,
                            it.mode
                        )
                    )
                    startActivity(intent)
                    finish()
                }
            }
        })

        btn_back.setOnClickListener {
            finish()
        }

        btn_settings.setOnClickListener {
            startActivity(WebViewActivity.newIntent(this))
        }
        if (!keyboardActivityArgs.retake) {
            showInfoDialog()
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {

        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_SHIFT_LEFT
            || keyCode == KeyEvent.KEYCODE_CAPS_LOCK || event == null
        ) {
            return super.onKeyUp(keyCode, event)
        }

        viewModel.handle(KeyboardViewAction.OnKeyInput(event.displayLabel))

        return super.onKeyUp(keyCode, event)
    }

    private fun showInfoDialog() {
        val alertLayout: View = getLayoutInflater().inflate(R.layout.layout_info_dialog, null)
        val btnAccept: View = alertLayout.findViewById(R.id.btn_ok)
        val btnClose: View = alertLayout.findViewById(R.id.btn_close)
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setView(alertLayout)
        builder.setCancelable(true)
        val btAlertDialog: AlertDialog? = builder.create()
        btAlertDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        btAlertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        btnAccept.setOnClickListener {
            if (btAlertDialog != null && btAlertDialog.isShowing) {
                btAlertDialog.dismiss()
            }
        }

        btnClose.setOnClickListener {
            if (btAlertDialog != null && btAlertDialog.isShowing) {
                btAlertDialog.dismiss()
            }
        }

        btAlertDialog?.show()
        btAlertDialog?.setWidthPercent(40);
    }

    private fun hideSystemUI() {
        if (window != null) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    override fun shouldInstallDynamicModule(): Boolean = true
}