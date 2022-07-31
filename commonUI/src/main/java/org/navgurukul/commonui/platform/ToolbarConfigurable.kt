package org.navgurukul.commonui.platform

import android.view.View
import android.widget.ImageView
import androidx.annotation.AttrRes
import androidx.appcompat.widget.Toolbar

interface ToolbarConfigurable {

    fun configure(toolbar: Toolbar)

    fun configure(
        title: String,
        @AttrRes colorRes: Int,
        showProfile: Boolean = false,
        subtitle: String? = null,
        onClickListener: View.OnClickListener? = null,
        action: String? = null,
        actionOnClickListener: View.OnClickListener? = null,
        showLogout: Boolean = false,
        showPathwayIcon: Boolean = false,
        pathwayIcon: String? = null
    )
}