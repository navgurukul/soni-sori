package org.navgurukul.learn.ui.learn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.navgurukul.learn.courses.db.models.CurrentStudy
import org.navgurukul.learn.courses.repository.LearnRepo

class LearnViewModel(private val learnRepo: LearnRepo) : ViewModel() {

    fun fetchCourseExerciseData(courseId: String) = liveData {
        emitSource(learnRepo.getCoursesExerciseData(courseId))
    }

    fun fetchCourseExerciseDataWithCourse(courseId: String) = liveData {
        emitSource(learnRepo.fetchCourseExerciseDataWithCourse(courseId))
    }

    fun fetchExerciseSlug(
        exerciseId: String,
        courseId: String,
        forceUpdate: Boolean
    ) = liveData {
        emitSource(learnRepo.getExerciseSlugData(exerciseId, courseId, forceUpdate))
    }

    fun saveCourseExerciseCurrent(
        currentStudy: CurrentStudy
    ) {
        viewModelScope.launch {
            learnRepo.saveCourseExerciseCurrent(currentStudy)
        }
    }
}