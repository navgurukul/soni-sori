package org.navgurukul.learn.ui.common

import android.app.Activity
import android.content.res.Resources
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import com.amulyakhare.textdrawable.TextDrawable
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