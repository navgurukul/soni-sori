package org.navgurukul.learn.ui.learn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.merakilearn.core.utils.CorePreferences
import org.navgurukul.learn.courses.db.models.CurrentStudy
import org.navgurukul.learn.courses.repository.LearnRepo

class LearnViewModel(private val learnRepo: LearnRepo, corePreferences: CorePreferences) : ViewModel() {

    private val selectedLanguage = corePreferences.selectedLanguage

    fun fetchCourseExerciseData(courseId: String) = liveData {
        emitSource(learnRepo.getCoursesExerciseData(courseId, selectedLanguage))
    }

    fun fetchCourseExerciseDataWithCourse(courseId: String) = liveData {
        emitSource(learnRepo.fetchCourseExerciseDataWithCourse(courseId, selectedLanguage))
    }

    fun fetchExerciseSlug(
        exerciseId: String,
        courseId: String,
        forceUpdate: Boolean,
    ) = liveData {
        emitSource(learnRepo.getExerciseSlugData(exerciseId, courseId, forceUpdate, selectedLanguage))
    }

    fun saveCourseExerciseCurrent(
        currentStudy: CurrentStudy
    ) {
        viewModelScope.launch {
            learnRepo.saveCourseExerciseCurrent(currentStudy)
        }
    }
}