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
import org.merakilearn.core.utils.CorePreferences

class ExerciseFragmentViewModel(
    private val learnRepo: LearnRepo,
    corePreferences: CorePreferences,
    private val stringProvider: StringProvider,
    private val args: ExerciseFragmentArgs
) : BaseViewModel<ExerciseFragmentViewModel.ExerciseFragmentViewEvents, ExerciseFragmentViewModel.ExerciseFragmentViewState>(
    ExerciseFragmentViewState()
) {

    private var fetchExerciseJob: Job? = null
    private val selectedLanguage = corePreferences.selectedLanguage

    init {
        fetchExerciseContent(args.exerciseId, "2000")
    }

    fun handle(action: ExerciseFragmentViewActions) {
        when (action) {
            is ExerciseFragmentViewActions.RequestContentRefresh -> fetchExerciseContent(
                args.exerciseId,
               "2000",
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

                if (list != null && list.content.isNotEmpty() == true) {
                    setState { copy(isError = false) }
                    setState { copy(exerciseList = it.content) }
                } else {
                    _viewEvents.setValue(
                        ExerciseFragmentViewEvents.ShowToast(
                            stringProvider.getString(
                                R.string.error_loading_data
                            )
                        )
                    )

                    setState { copy(isError = true) }
                }
            }
        }
    }

    sealed class ExerciseFragmentViewEvents : ViewEvents {
        class ShowToast(val toastText: String) : ExerciseFragmentViewEvents()
    }

    sealed class ExerciseFragmentViewActions : ViewModelAction {

        object RequestContentRefresh : ExerciseFragmentViewActions()

    }

    data class ExerciseFragmentViewState(
        val isLoading: Boolean = false,
        val isError: Boolean = false,
        val exerciseList: List<BaseCourseContent> = listOf()
    ) : ViewState

}