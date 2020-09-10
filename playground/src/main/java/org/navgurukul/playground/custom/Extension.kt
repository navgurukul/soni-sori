package org.navgurukul.playground.custom

import android.widget.EditText
import kotlin.math.max
import kotlin.math.min

// Add control String to the Cursor position
fun EditText.addTextAtCursorPosition(textToInsert: String){
    val start = max(this.selectionStart, 0)
    val end = max(this.selectionEnd, 0)
    this.text.replace(
        min(start, end), max(start, end),
        textToInsert, 0, textToInsert.length
    )
}