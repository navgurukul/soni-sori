package org.navgurukul.learn.ui.common

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.Decoration
import org.navgurukul.learn.courses.db.models.DecorationType

class DecorationView
constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    lateinit var decorView: TextView
    lateinit var content: TextView


    init {
        if(!isInEditMode) {
            inflate(context, R.layout.decoration_view, this)

            decorView = findViewById(R.id.decorStyle)
            content = findViewById(R.id.decorText)

        }
    }

    fun setDecoratedText(text: String, decor: Decoration){

        when(decor.type){
            DecorationType.bullet -> {
                decorView.text = "\u2022"
                content.text  = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT)
            }
            DecorationType.number -> {
                decorView.text = "${decor.value}."
                content.text  = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT)
            }
        }

    }
}