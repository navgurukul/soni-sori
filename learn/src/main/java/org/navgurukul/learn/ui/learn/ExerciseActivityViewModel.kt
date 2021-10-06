package org.navgurukul.learn.ui.learn

import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.ViewEvents
import org.navgurukul.commonui.platform.ViewModelAction
import org.navgurukul.commonui.platform.ViewState
import org.navgurukul.commonui.resources.StringProvider
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.Course
import org.navgurukul.learn.courses.db.models.Exercise
import org.navgurukul.learn.courses.repository.LearnRepo
import org.navgurukul.learn.util.LearnPreferences

class ExerciseActivityViewModel(
    private val learnRepo: LearnRepo,
    private val learnPreferences: LearnPreferences,
    private val stringProvider: StringProvider,
) : BaseViewModel<ExerciseActivityViewModel.ExerciseActivityViewEvents, ExerciseActivityViewModel.ExerciseActivityViewState>(
    ExerciseActivityViewState()
) {

    private val selectedLanguage = learnPreferences.selectedLanguage

    fun handle(action: ExerciseActivityViewActions) {
        when (action) {
            is ExerciseActivityViewActions.FetchCourseExercises -> fetchCourseExerciseData(
                action.courseId,
            )
            is ExerciseActivityViewActions.FetchCourseExerciseDataWithCourse -> fetchCourseExerciseDataWithCourse(
                action.courseId,
            )
        }
    }

    fun fetchCourseExerciseData(courseId: String){
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val response =
                learnRepo.getCoursesExerciseData(courseId, selectedLanguage)
            val list = response.value

            setState { copy(isLoading = false) }

            if (list != null) {
                _viewEvents.postValue(ExerciseActivityViewEvents.ShowExerciseList(list))
            } else {
                _viewEvents.postValue(
                    ExerciseActivityViewEvents.ShowToast(
                        stringProvider.getString(
                            R.string.error_loading_data
                        )
                    )
                )
            }
        }
    }

    fun fetchCourseExerciseDataWithCourse(courseId: String){
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val response =
                learnRepo.fetchCourseExerciseDataWithCourse(courseId, selectedLanguage)
            val list = response.value

            setState { copy(isLoading = false) }

            if (list != null) {
                _viewEvents.postValue(ExerciseActivityViewEvents.SetCourseAndRenderUI(list))
            } else {
                _viewEvents.postValue(
                    ExerciseActivityViewEvents.ShowToast(
                        stringProvider.getString(
                            R.string.error_loading_data
                        )
                    )
                )
            }
        }
    }

    sealed class ExerciseActivityViewEvents : ViewEvents {
        class SetCourseAndRenderUI(val courseList: List<Course>) : ExerciseActivityViewEvents()
        class ShowExerciseList(val exerciseList: List<Exercise>) : ExerciseActivityViewEvents()
        class ShowToast(val toastText: String) : ExerciseActivityViewEvents()
    }

    sealed class ExerciseActivityViewActions : ViewModelAction {
        data class FetchCourseExerciseDataWithCourse(
            val courseId: String,
        ) : ExerciseActivityViewActions()

        data class FetchCourseExercises(
            val courseId: String,
        ) : ExerciseActivityViewActions()

    }

    data class ExerciseActivityViewState(
        val isLoading: Boolean = false,
    ) : ViewState

}