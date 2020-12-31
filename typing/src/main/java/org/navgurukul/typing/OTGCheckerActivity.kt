package org.navgurukul.typing

import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_otg_checker.*
import org.json.JSONObject
import org.navgurukul.typing.dashboard.DashboardActivity


class OTGCheckerActivity : AppCompatActivity() {
    private var usbManager: UsbManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otg_checker)
        val i: Int
        if(isOtgSupported()) {
            i = R.id.compatible
        } else {
            i = R.id.not_compatible
        }
        (findViewById<RelativeLayout>(i)).visibility = View.VISIBLE

        btn_continue.setOnClickListener(View.OnClickListener {
            val dashboardActivity = Intent(this, DashboardActivity::class.java)
            startActivity(dashboardActivity)
            finish()
        })
    }

    private fun isOtgSupported(): Boolean {
        try {
            this.usbManager = getSystemService(Context.USB_SERVICE) as UsbManager
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        if (this.usbManager == null) {
            return false
        }
        try {
            return getPackageManager().hasSystemFeature("android.hardware.usb.host")
        } catch (e2: java.lang.Exception) {
            e2.printStackTrace()
        }
        return false
    }
}