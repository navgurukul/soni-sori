package org.merakilearn.core.core.dialogs

import androidx.annotation.AttrRes
import androidx.appcompat.app.AlertDialog
import org.navgurukul.chat.R
import org.navgurukul.commonui.themes.ThemeUtils

fun AlertDialog.withColoredButton(whichButton: Int, @AttrRes color: Int = com.google.android.material.R.attr.colorError): AlertDialog {
    getButton(whichButton)?.setTextColor(ThemeUtils.getColor(context, color))
    return this
}
