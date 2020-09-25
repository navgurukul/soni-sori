package org.merakilearn.ui.learn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.merakilearn.courses.db.models.CurrentStudy
import org.merakilearn.courses.repository.LearnRepo

class LearnViewModel(private val learnRepo: LearnRepo) : ViewModel() {

    fun fetchCourseData(forceUpdate: Boolean) = liveData {
        emitSource(learnRepo.getCoursesData(forceUpdate))
    }

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

    fun startDesiredActivity(courseId: String)= liveData {
        emit(learnRepo.fetchCurrentStudyForCourse(courseId))
    }


}