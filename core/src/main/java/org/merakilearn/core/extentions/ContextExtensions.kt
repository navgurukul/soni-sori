package org.merakilearn.core.extentions

import android.content.Context
import android.widget.Toast


// Not in KTX anymore
fun Context.toast(resId: Int) {
    Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
}

// Not in KTX anymore
fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}