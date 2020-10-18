package org.navgurukul.commonui.platform

import androidx.annotation.AttrRes
import androidx.appcompat.widget.Toolbar

interface ToolbarConfigurable {

    fun configure(toolbar: Toolbar)

    fun setTitle(title: String, @AttrRes colorRes: Int)
}