package org.navgurukul.learn.ui.common

import android.app.Activity
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
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

@BindingAdapter("imageUrl")
fun setImageUrl(imageView: ImageView, url: String?) {
    if (url == null) {
        imageView.setImageDrawable(ContextCompat.getDrawable(imageView.context,R.drawable.ic_learn))
    } else {
        val drawable = TextDrawable.builder()
            .beginConfig()
            .fontSize(14).toUpperCase().textColor(ContextCompat.getColor(imageView.context,R.color.colorNumber))
            .width(30)
            .height(30)
            .endConfig()
            .buildRound(url.toString(),ContextCompat.getColor(imageView.context,R.color.colorNumberBackground))
        imageView.setImageDrawable(drawable)
    }
}
