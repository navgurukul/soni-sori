package org.navgurukul.learn.ui.common

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButton
import org.navgurukul.learn.R

class CourseExerciseNavigationBottomSheet
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    lateinit var btnNext: MaterialButton
    lateinit var btnPrev: MaterialButton
    lateinit var btnMain: MaterialButton

    init {
        if (!isInEditMode) {
            inflate(context, R.layout.course_exercise_navigation_sheet_content, this)

            btnNext = findViewById(R.id.navigateNext)
            btnPrev = findViewById(R.id.navigatePrev)
            btnMain = findViewById(R.id.btnMain)

        }
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }

    fun setNavigationActions(prevAction: () -> Unit, nextAction: () -> Unit) {
        btnPrev.setOnClickListener {
            prevAction.invoke()
        }

        btnNext.setOnClickListener {
            nextAction.invoke()
        }
    }

    fun setView(isFirstItem: Boolean) {

        if (isFirstItem)
            btnPrev.visibility = View.GONE
        else
            btnPrev.visibility = View.VISIBLE

    }

    fun setMainButton(btnText: String, btnAction: () -> Unit, isOnlyMainBtnVisible: Boolean) {
        btnMain.visibility = View.VISIBLE

        btnMain.text = btnText

        btnMain.setOnClickListener {
            btnAction.invoke()
        }

        if (isOnlyMainBtnVisible) {
            btnPrev.visibility = View.GONE
            btnNext.visibility = View.GONE
        }
    }


}