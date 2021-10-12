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
import org.navgurukul.learn.courses.db.models.BaseCourseContent
import org.navgurukul.learn.courses.db.models.CurrentStudy
import org.navgurukul.learn.courses.repository.LearnRepo
import org.navgurukul.learn.util.LearnPreferences

class ExerciseFragmentViewModel(
    private val learnRepo: LearnRepo,
    learnPreferences: LearnPreferences,
    private val stringProvider: StringProvider,
    private val args: ExerciseFragmentArgs
) : BaseViewModel<ExerciseFragmentViewModel.ExerciseFragmentViewEvents, ExerciseFragmentViewModel.ExerciseFragmentViewState>(
    ExerciseFragmentViewState()
) {

    private var fetchExerciseJob: Job? = null
    private val selectedLanguage = learnPreferences.selectedLanguage

    init {
        fetchExerciseContent(args.exerciseId, args.courseId)
    }

    fun handle(action: ExerciseFragmentViewActions) {
        when (action) {
            is ExerciseFragmentViewActions.ScreenRendered -> saveCourseExerciseCurrent(action.currentStudy)
            is ExerciseFragmentViewActions.MarkCompleteClicked -> markCourseExerciseCompleted(action.currentStudy)
            is ExerciseFragmentViewActions.PulledDownToRefresh -> fetchExerciseContent(
                args.exerciseId,
                args.courseId,
                true
            )
        }
    }

    private fun fetchExerciseContent(
        exerciseId: String,
        courseId: String,
        forceUpdate: Boolean = false,
    ) {
        fetchExerciseJob?.cancel()
        fetchExerciseJob = viewModelScope.launch {
            setState { copy(isLoading = true) }
            learnRepo.getExerciseContents(
                exerciseId,
                courseId,
                forceUpdate,
                selectedLanguage
            ).collect {
                val list = it

                setState { copy(isLoading = false) }

                if (list != null && list.content?.isNotEmpty() == true) {
                    setState { copy(exerciseList = it.content!!) }
                } else {
                    _viewEvents.setValue(
                        ExerciseFragmentViewEvents.ShowToast(
                            stringProvider.getString(
                                R.string.error_loading_data
                            )
                        )
                    )
                }
            }
        }
    }

    private fun saveCourseExerciseCurrent(
        currentStudy: CurrentStudy
    ) {
        viewModelScope.launch {
            learnRepo.saveCourseExerciseCurrent(currentStudy)
        }
    }

    private fun markCourseExerciseCompleted(
        currentStudy: CurrentStudy
    ) {
        viewModelScope.launch {
            learnRepo.markCourseExerciseCompleted(currentStudy)
        }
    }

    sealed class ExerciseFragmentViewEvents : ViewEvents {
        class ShowToast(val toastText: String) : ExerciseFragmentViewEvents()
    }

    sealed class ExerciseFragmentViewActions : ViewModelAction {
        data class ScreenRendered(val currentStudy: CurrentStudy) : ExerciseFragmentViewActions()
        data class MarkCompleteClicked(val currentStudy: CurrentStudy) :
            ExerciseFragmentViewActions()

        object PulledDownToRefresh : ExerciseFragmentViewActions()

    }

    data class ExerciseFragmentViewState(
        val isLoading: Boolean = false,
        val exerciseList: List<BaseCourseContent> = listOf()
    ) : ViewState

}