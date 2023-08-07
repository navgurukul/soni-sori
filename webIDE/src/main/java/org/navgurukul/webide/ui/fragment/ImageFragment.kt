package org.navgurukul.webide.ui.fragment

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import org.navgurukul.webide.R
import org.navgurukul.webide.extensions.action
import org.navgurukul.webide.extensions.snack
import org.navgurukul.webide.util.editor.ResourceHelper
import java.io.File

class ImageFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val location = requireArguments().getString("location")
        var file: File? = null
        location?.let {
            file = File(location)
        }

        file?.let {
            if (!file!!.exists()) {
                val textView = TextView(activity)
                val padding = ResourceHelper.dpToPx(requireActivity(), 48)
                textView.setPadding(padding, padding, padding, padding)
                textView.gravity = Gravity.CENTER
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_alert_error, 0, 0, 0)
                textView.setText(R.string.file_problem)
                return textView
            }
        }

        val drawable = BitmapDrawable(requireActivity().resources, file!!.absolutePath)
        val imageView = ImageView(activity)
        val fileSize = getSize(file!!)
        imageView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
        imageView.setImageDrawable(drawable)
        imageView.setOnClickListener {
            imageView.snack(
                    "${drawable.intrinsicWidth} x ${drawable.intrinsicHeight}px $fileSize",
                    Snackbar.LENGTH_INDEFINITE) {
                action("OK") { dismiss() }
            }
        }

        return imageView
    }

    private fun getSize(f: File): String {
        val size = f.length() / 1024
        return if (size >= 1024) {
            (size / 1024).toString() + " MB"
        } else {
            size.toString() + " KB"
        }
    }

    companion object {

        fun newInstance(args: Bundle) = ImageFragment().apply {
            arguments = args
        }
    }
}
