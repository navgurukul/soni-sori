package org.merakilearn.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import org.merakilearn.datasource.ApplicationRepo

class HomeViewModel(private val applicationRepo:ApplicationRepo) : ViewModel() {

    fun fetchWhereYouLeftData() = liveData {
        emitSource(applicationRepo.fetchWhereYouLeftData())
    }

    fun fetchOtherCourseData()= liveData {
        emit(applicationRepo.fetchOtherCourseData())
    }


    fun fetchMyClasses() = liveData {
        emit(applicationRepo.fetchMyClassData())
    }
}