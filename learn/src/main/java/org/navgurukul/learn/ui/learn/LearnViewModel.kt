package org.navgurukul.learn.ui.learn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.navgurukul.learn.courses.repository.CoursesRepositoryImpl

class LearnViewModel(private val repository: CoursesRepositoryImpl) : ViewModel() {

    val courses = repository.allCourses

    fun fetchCourses() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.fetchCoursesFromApi()
            }
        }
    }

    class Factory(private val repository: CoursesRepositoryImpl) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LearnViewModel::class.java)) {
                return LearnViewModel(repository) as T
            }

            throw IllegalArgumentException("LearnViewModel cannot be created")
        }
    }

}