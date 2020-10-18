package org.navgurukul.chat.features.popup

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import com.tapadoo.alerter.Alerter
import com.tapadoo.alerter.OnHideAlertListener
import org.koin.java.KoinJavaComponent.inject
import org.navgurukul.chat.ChatBaseActivity
import org.navgurukul.chat.R
import org.navgurukul.chat.features.home.AvatarRenderer
import timber.log.Timber
import java.lang.ref.WeakReference

class PopupAlertManager {

    private val avatarRenderer: AvatarRenderer by inject(AvatarRenderer::class.java)

    private var weakCurrentActivity: WeakReference<Activity>? = null
    private var currentAlerter: SaralAlert? = null

    private val alertFiFo = ArrayList<SaralAlert>()

    fun postVectorAlert(alert: SaralAlert) {
        synchronized(alertFiFo) {
            alertFiFo.add(alert)
        }
        weakCurrentActivity?.get()?.runOnUiThread {
            displayNextIfPossible()
        }
    }

    fun cancelAlert(uid: String) {
        synchronized(alertFiFo) {
            alertFiFo.listIterator().apply {
                while (this.hasNext()) {
                    val next = this.next()
                    if (next.uid == uid) {
                        this.remove()
                    }
                }
            }
        }

        // it could also be the current one
        if (currentAlerter?.uid == uid) {
            weakCurrentActivity?.get()?.runOnUiThread {
                Alerter.hide()
                currentIsDismissed()
            }
        }
    }

    fun onNewActivityDisplayed(activity: Activity) {
        // we want to remove existing popup on previous activity and display it on new one
        if (currentAlerter != null) {
            weakCurrentActivity?.get()?.let {
                Alerter.clearCurrent(it)
                setLightStatusBar()
            }
        }
        if (currentAlerter?.shouldBeDisplayedIn?.invoke(activity) == false || activity !is ChatBaseActivity) {
            return
        }

        weakCurrentActivity = WeakReference(activity)

        if (currentAlerter != null) {
            if (currentAlerter!!.expirationTimestamp != null && System.currentTimeMillis() > currentAlerter!!.expirationTimestamp!!) {
                // this alert has expired, remove it
                // perform dismiss
                try {
                    currentAlerter?.dismissedAction?.run()
                } catch (e: Exception) {
                    Timber.e("## failed to perform action")
                }
                currentAlerter = null
                Handler(Looper.getMainLooper()).postDelayed({
                    displayNextIfPossible()
                }, 2000)
            } else {
                showAlert(currentAlerter!!, activity, animate = false)
            }
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                displayNextIfPossible()
            }, 2000)
        }
    }

    private fun displayNextIfPossible() {
        val currentActivity = weakCurrentActivity?.get()
        if (Alerter.isShowing || currentActivity == null) {
            // will retry later
            return
        }
        val next: SaralAlert?
        synchronized(alertFiFo) {
            next = alertFiFo.firstOrNull()
            if (next != null) alertFiFo.remove(next)
        }
        currentAlerter = next
        next?.let {
            if (next.shouldBeDisplayedIn?.invoke(currentActivity) == false) return
            val currentTime = System.currentTimeMillis()
            if (next.expirationTimestamp != null && currentTime > next.expirationTimestamp!!) {
                // skip
                try {
                    next.dismissedAction?.run()
                } catch (e: java.lang.Exception) {
                    Timber.e("## failed to perform action")
                }
                displayNextIfPossible()
            } else {
                showAlert(it, currentActivity)
            }
        }
    }

    @SuppressLint("InlinedApi")
    private fun clearLightStatusBar() {
        weakCurrentActivity?.get()
                ?.takeIf { Build.VERSION.SDK_INT >= Build.VERSION_CODES.M }
                // Do not change anything on Dark themes
                ?.let { it.window?.decorView }
                ?.let { view ->
                    var flags = view.systemUiVisibility
                    flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                    view.systemUiVisibility = flags
                }
    }

    @SuppressLint("InlinedApi")
    private fun setLightStatusBar() {
        weakCurrentActivity?.get()
                ?.takeIf { Build.VERSION.SDK_INT >= Build.VERSION_CODES.M }
                // Do not change anything on Dark themes
                ?.let { it.window?.decorView }
                ?.let { view ->
                    var flags = view.systemUiVisibility
                    flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    view.systemUiVisibility = flags
                }
    }

    private fun showAlert(alert: SaralAlert, activity: Activity, animate: Boolean = true) {
        clearLightStatusBar()

        alert.weakCurrentActivity = WeakReference(activity)
        val alerter = if (alert is VerificationSaralAlert) Alerter.create(activity, R.layout.alerter_verification_layout)
        else Alerter.create(activity)

        alerter.setTitle(alert.title)
                .setText(alert.description)
                .also { al ->
                    if (alert is VerificationSaralAlert) {
                        val tvCustomView = al.getLayoutContainer()
                        tvCustomView?.findViewById<ImageView>(R.id.ivUserAvatar)?.let { imageView ->
                            alert.matrixItem?.let { avatarRenderer.render(it, imageView) }
                        }
                    }
                }
                .apply {
                    if (!animate) {
                        setEnterAnimation(R.anim.anim_alerter_no_anim)
                    }

                    alert.iconId?.let {
                        setIcon(it)
                    }
                    alert.actions.forEach { action ->
                        addButton(action.title, R.style.AlerterButton, View.OnClickListener {
                            if (action.autoClose) {
                                currentIsDismissed()
                                Alerter.hide()
                            }
                            try {
                                action.action.run()
                            } catch (e: java.lang.Exception) {
                                Timber.e("## failed to perform action")
                            }
                        })
                    }
                    setOnClickListener(View.OnClickListener { _ ->
                        alert.contentAction?.let {
                            currentIsDismissed()
                            Alerter.hide()
                            try {
                                it.run()
                            } catch (e: java.lang.Exception) {
                                Timber.e("## failed to perform action")
                            }
                        }
                    })
                }
                .setOnHideListener(OnHideAlertListener {
                    // called when dismissed on swipe
                    try {
                        alert.dismissedAction?.run()
                    } catch (e: java.lang.Exception) {
                        Timber.e("## failed to perform action")
                    }
                    currentIsDismissed()
                })
                .enableSwipeToDismiss()
                .enableInfiniteDuration(true)
                .apply {
                    if (alert.colorInt != null) {
                        setBackgroundColorInt(alert.colorInt!!)
                    } else {
                        setBackgroundColorRes(alert.colorRes ?: R.color.notification_accent_color)
                    }
                }
                .show()
    }

    private fun currentIsDismissed() {
        // current alert has been hidden
        setLightStatusBar()

        currentAlerter = null
        Handler(Looper.getMainLooper()).postDelayed({
            displayNextIfPossible()
        }, 500)
    }
}