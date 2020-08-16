package org.navgurukul.learn.ui.learn

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.navgurukul.learn.datasource.LearnRepo

class LearnViewModel(private val learnRepo: LearnRepo) : ViewModel() {

    private var isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    var showLoadingIndicator: LiveData<Boolean> = Transformations.map(isLoading) { isLoading.value }


    val coursesData = learnRepo.courses

    init {
        fetchCourseData()
    }

    private fun fetchCourseData() {
        viewModelScope.launch {
            isLoading.postValue(true)
            learnRepo.getCourses()
            isLoading.postValue(false)
        }
    }

}