package org.navgurukul.learn.ui.common

import android.app.Activity
import android.content.res.Resources
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import com.amulyakhare.textdrawable.TextDrawable
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.ExerciseProgress
import org.navgurukul.learn.courses.db.models.ExerciseType


fun Activity.toast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(text: String) {
    Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
}

fun Activity.toolbarColor(): Int {
    return ContextCompat.getColor(
        this,
        R.color.colorBlack
    )
}

@BindingAdapter(value = ["exerciseType","exerciseProgress"])
fun setImageUrl(imageView: ImageView, type: ExerciseType?, progress: ExerciseProgress) {
    if (type == null) {
        imageView.setImageDrawable(ContextCompat.getDrawable(imageView.context,R.drawable.ic_learn))
    } else {
        val layoutId = when(type){
            ExerciseType.TEXT -> {
                when (progress) {
                    ExerciseProgress.COMPLETED -> R.drawable.ic_exercise_complete
                    ExerciseProgress.IN_PROGRESS -> R.drawable.ic_exercise_in_progress
                    ExerciseProgress.NOT_STARTED -> R.drawable.ic_exercise_not_started
                    else -> R.drawable.ic_exercise_not_started
                }
            }
            else -> R.drawable.ic_exercise_not_started
        }
        imageView.setBackgroundResource(layoutId)
    }
}
