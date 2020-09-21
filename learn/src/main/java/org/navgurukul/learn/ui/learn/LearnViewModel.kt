package org.navgurukul.learn.ui.learn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.navgurukul.learn.courses.db.models.CurrentStudy
import org.navgurukul.learn.courses.repository.LearnRepo

class LearnViewModel(private val learnRepo: LearnRepo) : ViewModel() {

    fun fetchCourseData() = liveData {
        emitSource(learnRepo.getCoursesData())
    }

    fun fetchCourseExerciseData(courseId: String) = liveData {
        emitSource(learnRepo.getCoursesExerciseData(courseId))
    }

    fun fetchExerciseSlug(courseId: String, slug: String) = liveData {
        emitSource(learnRepo.getExerciseSlugData(courseId, slug))
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