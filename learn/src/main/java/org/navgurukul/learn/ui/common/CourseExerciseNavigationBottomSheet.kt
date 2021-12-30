package org.navgurukul.learn.ui.common

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.time_next_warning.view.*
import kotlinx.android.synthetic.main.time_prev_warning.*
import kotlinx.android.synthetic.main.time_prev_warning.view.*
import org.navgurukul.learn.R



class CourseExerciseNavigationBottomSheet


@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {


    private val btnNext: MaterialButton
    private val btnPrev: MaterialButton
    private val btnMain: MaterialButton




    init {
        inflate(context, R.layout.course_exercise_navigation_sheet_content, this)

        btnNext = findViewById(R.id.navigateNext)
        btnPrev = findViewById(R.id.navigatePrev)
        btnMain = findViewById(R.id.btnMain)

    }

    fun setNavigationActions(prevAction: () -> Unit, nextAction: () -> Unit) {
         val start = System.currentTimeMillis()
//            val end = System.currentTimeMillis() -start



        btnPrev.setOnClickListener {

            val mDialogView= LayoutInflater.from(context).inflate(R.layout.time_prev_warning, null)
            val mBuilder = AlertDialog.Builder(context)
                .setView(mDialogView)

            val mAlertDialog = mBuilder.show()

            mDialogView.btnStayBack.setOnClickListener {
                mAlertDialog.dismiss()
            }

            mAlertDialog.nextBtnBack.setOnClickListener {
                mAlertDialog.dismiss()
                prevAction.invoke()
            }
//            prevAction.invoke()

        }

            btnNext.setOnClickListener{
                val end = System.currentTimeMillis() - start
                Log.d("dhanshri", "endTime    "+end)
                Log.d("dhanshri", "btnNextCheck    "+end)


                val mDialogView = LayoutInflater.from(context).inflate(R.layout.time_next_warning,null)
                val mBuilder = AlertDialog.Builder(context)
                        .setView(mDialogView)

                val mAlertDialog = mBuilder.show()

                    mDialogView.btnStay.setOnClickListener{
                        mAlertDialog.dismiss()
                    }

                    mDialogView.nextBtn.setOnClickListener {
                        mAlertDialog.dismiss()
                        nextAction.invoke()
                    }

        } 
    }

    fun updateNavButtons(isFirstItem: Boolean) {
        btnMain.visibility = View.GONE
        btnNext.visibility = View.VISIBLE
        if (isFirstItem) {
            btnPrev.visibility = View.GONE
        } else {
            btnPrev.visibility = View.VISIBLE
        }

    }

    fun setMainButton(btnText: String, btnAction: () -> Unit) {
        btnMain.visibility = View.VISIBLE

        btnMain.text = btnText

        btnMain.setOnClickListener {
            btnAction.invoke()
        }

        btnPrev.visibility = View.GONE
        btnNext.visibility = View.GONE
    }
}