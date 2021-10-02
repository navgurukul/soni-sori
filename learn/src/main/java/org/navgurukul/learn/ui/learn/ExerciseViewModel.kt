package org.navgurukul.learn.ui.learn

import androidx.lifecycle.ViewModel
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.learn.courses.repository.LearnRepo
import org.navgurukul.learn.util.LearnPreferences

class ExerciseViewModel( private val learnRepo: LearnRepo, private val learnPreferences: LearnPreferences)
    : ViewModel(){
//    : BaseViewModel<ExerciseFragmentViewEvents, ExerciseViewState>(initialState){
}