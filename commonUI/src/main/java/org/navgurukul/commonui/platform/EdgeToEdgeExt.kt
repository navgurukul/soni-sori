package org.navgurukul.commonui.platform

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Setup edge-to-edge display for Android 15+ devices
 * This function handles top, bottom, start, and end insets to prevent UI cutoff
 */
fun AppCompatActivity.setupEdgeToEdge() {
    WindowCompat.setDecorFitsSystemWindows(window, false)

    val rootView = window.decorView.findViewById<ViewGroup>(android.R.id.content)

    // Apply window insets listener to handle all insets (top, bottom, start, end)
    ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

        // Apply padding for top, bottom, start (left), and end (right)
        view.setPadding(
            insets.left,
            insets.top,
            insets.right,
            insets.bottom
        )

        // Return the insets so they can be handled by other listeners as well
        windowInsets
    }
}

/**
 * Setup edge-to-edge display for a specific view/fragment container
 * Handles top, bottom, start, and end insets to prevent UI cutoff
 */
fun View.setupEdgeToEdgeForView() {
    // Apply window insets listener to handle all insets
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

        // Apply padding for top, bottom, start (left), and end (right)
        view.setPadding(
            insets.left,
            insets.top,
            insets.right,
            insets.bottom
        )

        // Return the insets so they can be handled by other listeners as well
        windowInsets
    }
}
