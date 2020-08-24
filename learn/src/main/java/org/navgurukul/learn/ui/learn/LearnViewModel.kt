package org.navgurukul.learn.ui.learn

import android.content.Context
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.navgurukul.learn.courses.db.models.CurrentStudy
import org.navgurukul.learn.courses.repository.LearnRepo

class LearnViewModel(private val learnRepo: LearnRepo) : ViewModel() {

    private var isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    var showLoadingIndicator: LiveData<Boolean> = Transformations.map(isLoading) { isLoading.value }

    fun hideLoader() {
        isLoading.postValue(false)
    }

    fun fetchCourseData() = liveData {
        isLoading.postValue(true)
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