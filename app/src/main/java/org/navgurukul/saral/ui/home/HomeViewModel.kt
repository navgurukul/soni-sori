package org.navgurukul.saral.ui.home

import androidx.lifecycle.*
import org.navgurukul.saral.datasource.ApplicationRepo

class HomeViewModel(private val applicationRepo:ApplicationRepo) : ViewModel() {

    fun fetchWhereYouLeftData() = liveData {
        emitSource(applicationRepo.fetchWhereYouLeftData())
    }

    fun fetchOtherCourseData()= liveData {
        emit(applicationRepo.fetchOtherCourseData())
    }

    fun fetchUpcomingClass()= liveData {
        emit(applicationRepo.fetchUpcomingClassData())
    }

    fun fetchMyClasses()= liveData {
        emit(applicationRepo.fetchMyClassData())
    }

    fun enrollToClass(classId: Int, enrolled: Boolean) = liveData {
        emit(applicationRepo.enrollToClass(classId,enrolled))
    }
}