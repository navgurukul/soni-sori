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

    lateinit var btnMarkAsCompleted: MaterialButton
    lateinit var btnNext: ImageView
    lateinit var btnPrev: ImageView

    init {
        if(!isInEditMode) {
            inflate(context, R.layout.course_exercise_navigation_sheet_content, this)

            btnMarkAsCompleted = findViewById(R.id.btnMarkCompleted)
            btnNext = findViewById(R.id.navigateNext)
            btnPrev = findViewById(R.id.navigatePrev)

        }
    }

        companion object {
            const val TAG = "ModalBottomSheet"
        }

    fun setMarkAction(markAction: () -> Unit){
        btnMarkAsCompleted.setOnClickListener{
            markAction.invoke()
        }
    }

    fun setNavigationActions(prevAction: () -> Unit, nextAction: () -> Unit){
        btnPrev.setOnClickListener{
            prevAction.invoke()
        }

        btnNext.setOnClickListener{
            nextAction.invoke()
        }
    }

    fun setView(isMarkedCompleted: Boolean, isFirstItem: Boolean, isLastItem: Boolean){

        if(isMarkedCompleted) {
            btnMarkAsCompleted.visibility = View.GONE

            if(isFirstItem)
                btnPrev.visibility = View.GONE
            else
                btnPrev.visibility = View.VISIBLE

            if(isLastItem)
                btnNext.visibility = View.GONE
            else
                btnNext.visibility = View.VISIBLE
        }
        else {
            btnMarkAsCompleted.visibility = View.VISIBLE

            btnPrev.visibility = View.GONE
            btnNext.visibility = View.GONE
        }


    }


}