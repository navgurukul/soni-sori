package org.navgurukul.webide.extensions

import android.widget.EditText
import com.google.android.material.textfield.TextInputEditText

fun EditText.string() = text.toString()

fun TextInputEditText.string() = text.toString()
