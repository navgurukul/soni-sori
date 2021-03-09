package org.navgurukul.commonui.platform

import android.view.View
import androidx.annotation.AttrRes
import androidx.appcompat.widget.Toolbar

interface ToolbarConfigurable {

    fun configure(toolbar: Toolbar)

    fun configure(
        title: String,
        subtitle: String?,
        @AttrRes colorRes: Int,
        showProfile: Boolean = false,
        onClickListener: View.OnClickListener? = null
    )
}