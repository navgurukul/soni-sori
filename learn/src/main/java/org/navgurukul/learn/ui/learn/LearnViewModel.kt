package org.navgurukul.learn.ui.learn

import androidx.lifecycle.*
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
        learnRepo.saveCourseExerciseCurrent(currentStudy)
    }

    fun fetchCurrentStudyForCourse(courseId: String, callback: (List<CurrentStudy>) -> Unit) {
        learnRepo.fetchCurrentStudyForCourse(courseId, callback)
    }

}