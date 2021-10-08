package org.navgurukul.learn.ui.learn

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.ViewEvents
import org.navgurukul.commonui.platform.ViewModelAction
import org.navgurukul.commonui.platform.ViewState
import org.navgurukul.commonui.resources.StringProvider
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.CurrentStudy
import org.navgurukul.learn.courses.db.models.Exercise
import org.navgurukul.learn.courses.repository.LearnRepo
import org.navgurukul.learn.util.LearnPreferences

class ExerciseFragmentViewModel(private val learnRepo: LearnRepo,
                                private val learnPreferences: LearnPreferences,
                                private val stringProvider: StringProvider,
                                private val courseId: String,
                                private val exerciseId: String,)
    : BaseViewModel<ExerciseFragmentViewModel.ExerciseFragmentViewEvents, ExerciseFragmentViewModel.ExerciseFragmentViewState>(
    ExerciseFragmentViewState()
){

    private var fetchExerciseJob: Job? = null
    private val selectedLanguage = learnPreferences.selectedLanguage

    init {
        fetchExerciseSlug(exerciseId, courseId)
    }

    fun handle(action: ExerciseFragmentViewActions) {
        when (action) {
            is ExerciseFragmentViewActions.SaveCourseExerciseFragmentCurrent -> saveCourseExerciseCurrent(action.currentStudy)
            is ExerciseFragmentViewActions.FetchExerciseFragmentSlug -> fetchExerciseSlug(action.exerciseId, action.courseId, action.forceUpdate)
        }
    }

    fun fetchExerciseSlug(
        exerciseId: String,
        courseId: String,
        forceUpdate: Boolean = false,
    ) {
        fetchExerciseJob?.cancel()
        fetchExerciseJob = viewModelScope.launch {
            setState { copy(isLoading = true) }
            val response =
                learnRepo.getExerciseSlugData(
                    exerciseId,
                    courseId,
                    forceUpdate,
                    selectedLanguage
                ).collect {
                    val list = it

                    setState { copy(isLoading = false) }

                    if(list.isNotEmpty()){
                        _viewEvents.setValue(ExerciseFragmentViewEvents.ShowExercise(list))
                    }else {
                        _viewEvents.setValue(ExerciseFragmentViewEvents.ShowToast(stringProvider.getString(R.string.error_loading_data)))
                    }
                }
        }
    }

    fun saveCourseExerciseCurrent(
        currentStudy: CurrentStudy
    ) {
        viewModelScope.launch {
            learnRepo.saveCourseExerciseCurrent(currentStudy)
        }
    }

    sealed class ExerciseFragmentViewEvents : ViewEvents {
        class ShowExercise(val exerciseList: List<Exercise>) : ExerciseFragmentViewEvents()
        class ShowToast(val toastText: String) : ExerciseFragmentViewEvents()
    }

    sealed class ExerciseFragmentViewActions : ViewModelAction {
        data class SaveCourseExerciseFragmentCurrent(val currentStudy: CurrentStudy) : ExerciseFragmentViewActions()
        data class FetchExerciseFragmentSlug(val exerciseId: String,
                                             val courseId: String,
                                             val forceUpdate: Boolean,) : ExerciseFragmentViewActions()

    }

    data class ExerciseFragmentViewState(
        val isLoading: Boolean = false,
    ) : ViewState

}