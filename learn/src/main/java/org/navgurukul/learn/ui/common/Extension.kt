package org.navgurukul.learn.ui.common

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import androidx.annotation.IntRange
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import org.navgurukul.learn.R


fun Activity.toast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(text: String) {
    Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
}

fun Activity.toolbarColor(): Int {
    return ContextCompat.getColor(
        this,
        R.color.colorBlack
    )
}


