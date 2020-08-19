package org.navgurukul.learn.ui.learn

import androidx.lifecycle.*
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
        isLoading.postValue(true)
        emitSource(learnRepo.getCoursesExerciseData(courseId))
    }
}