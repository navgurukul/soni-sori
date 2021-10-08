package org.navgurukul.learn.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.navgurukul.learn.R

class CourseExerciseNavigationBottomSheet: BottomSheetDialogFragment() {

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? = inflater.inflate(R.layout.course_exercise_navigation_sheet_content, container, false)

        companion object {
            const val TAG = "ModalBottomSheet"
        }

}