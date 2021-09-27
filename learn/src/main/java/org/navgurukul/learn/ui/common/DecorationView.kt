package org.navgurukul.learn.ui.common

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.Decoration
import org.navgurukul.learn.courses.db.models.DecorationType

class DecorationView
@JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    lateinit var decorView: TextView

    init {
        if(!isInEditMode) {
            inflate(context, R.layout.decoration_view, this)

            decorView = findViewById(R.id.decorStyle)

        }
    }

    fun setDecor(decor: Decoration){

        when(decor.type){
            DecorationType.bullet -> {
                decorView.visibility = View.VISIBLE
                decorView.text = "\u2022\t\t"
                decorView.setTypeface(null, Typeface.BOLD)
            }
            DecorationType.number -> {
                decorView.visibility = View.VISIBLE
                decorView.text = "${decor.value}.\t\t"
                decorView.setTypeface(null, Typeface.NORMAL);
            }
            else -> {
                decorView.visibility = View.GONE
            }
        }

    }
}