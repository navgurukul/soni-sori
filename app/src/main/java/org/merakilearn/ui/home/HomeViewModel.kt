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

    fun fetchUpcomingClass(langCode: String?) = liveData {
        emit(applicationRepo.fetchUpcomingClassData(langCode))
    }

    fun fetchMyClasses() = liveData {
        emit(applicationRepo.fetchMyClassData())
    }

    fun enrollToClass(classId: Int, enrolled: Boolean) = liveData {
        emit(applicationRepo.enrollToClass(classId, enrolled))
    }

    fun fetchClassData(classId: String) = liveData {
        emit(applicationRepo.fetchClassData(classId))
    }
}