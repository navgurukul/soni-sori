package org.navgurukul.typingguru.navigator

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import com.google.auto.service.AutoService
import org.merakilearn.core.navigator.TypingAppModuleNavigator
import org.navgurukul.typingguru.R
import org.navgurukul.typingguru.ui.KeyboardActivity
import org.navgurukul.typingguru.ui.WebViewActivity
import org.navgurukul.typingguru.utils.Logger
import org.navgurukul.typingguru.utils.TypingGuruPreferenceManager
import org.navgurukul.typingguru.utils.Utility

@AutoService(TypingAppModuleNavigator::class)
class TypingAppModuleNavigatorImpl : TypingAppModuleNavigator {
    
    override fun launchTypingApp(activity: Activity, mode : TypingAppModuleNavigator.Mode) { //content: ArrayList<String>, code: String
        TypingGuruPreferenceManager.instance().init(activity)
        if (TypingGuruPreferenceManager.instance().iWebViewShown()) {
            activity.startActivity(KeyboardActivity.newIntent(activity, mode))
        } else {
            showKeyboardPopup(activity, Utility.isOtgSupported(activity), {
                activity.startActivity(KeyboardActivity.newIntent(activity, mode))
            }, {
                activity.startActivity(Intent(activity, WebViewActivity::class.java))
            })
        }

    }

    private fun showKeyboardPopup(
        context: Context,
        isOtgSupported : Boolean,
        btnOwnClicked: () -> Unit,
        btnPurchaseClicked: () -> Unit
    ) {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val alertLayout: View = inflater.inflate(R.layout.layout_keyboard_dialog, null)
        val viewOtg: TextView = alertLayout.findViewById(R.id.txt_lbl_info3) as TextView
        if (isOtgSupported) {
            viewOtg.text = context.getString(R.string.otg_support)
        } else {
            viewOtg.text = context.getString(R.string.no_otg_support)
        }
        val btnOwn: AppCompatButton = alertLayout.findViewById(R.id.btn_own) as AppCompatButton
        val btnPurchase: AppCompatButton =
            alertLayout.findViewById(R.id.btn_purchase) as AppCompatButton
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setView(alertLayout)
        builder.setCancelable(true)
        val btAlertDialog: AlertDialog = builder.create()
        btAlertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        btAlertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        btAlertDialog.setCancelable(false)
        btnOwn.setOnClickListener {
            if (btAlertDialog.isShowing) {
                btAlertDialog.dismiss()
                TypingGuruPreferenceManager.instance().setWebViewDisplayStatus(true)
                btnOwnClicked()
            }
        }
        btnPurchase.setOnClickListener {
            if (btAlertDialog.isShowing) {
                btAlertDialog.dismiss()
                TypingGuruPreferenceManager.instance().setWebViewDisplayStatus(true)
                btnPurchaseClicked()
            }
        }
        btAlertDialog.show()
    }
}