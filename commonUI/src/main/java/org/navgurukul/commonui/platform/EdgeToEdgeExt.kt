package org.navgurukul.commonui.platform

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Setup edge-to-edge display for Android 15+ devices
 * This function handles bottom and top insets to prevent UI cutoff
 */
fun AppCompatActivity.setupEdgeToEdge() {
    WindowCompat.setDecorFitsSystemWindows(window, false)

    val rootView = window.decorView.findViewById<ViewGroup>(android.R.id.content)

    // Apply window insets listener to handle bottom and top padding
    ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

        // Apply padding for top and bottom only
        view.setPadding(
            view.paddingLeft,
            insets.top,
            view.paddingRight,
            insets.bottom
        )

        // Return the insets so they can be handled by other listeners as well
        windowInsets
    }
}

/**
 * Setup edge-to-edge display for a specific view/fragment container
 * Handles bottom and top insets to prevent UI cutoff
 */
fun View.setupEdgeToEdgeForView() {
    // Apply window insets listener to handle bottom and top padding
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

        // Apply padding for top and bottom only (not start/end)
        view.setPadding(
            view.paddingLeft,
            insets.top,
            view.paddingRight,
            insets.bottom
        )

        // Return the insets so they can be handled by other listeners as well
        windowInsets
    }
}
