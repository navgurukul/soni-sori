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
import org.navgurukul.learn.courses.repository.LearnRepoNew

class ExerciseWordFragmentViewModel{
//    private val learnRepo: LearnRepoNew,
//    corePreferences: CorePreferences,
//    private val stringProvider: StringProvider,
//    private val args: ExerciseWordFragmentArgs
//) : BaseViewModel<ExerciseWordFragmentViewModel.ExerciseWordFragmentViewEvents, ExerciseWordFragmentViewModel.ExerciseWordFragmentViewState>(
//    ExerciseWordFragmentViewState()
//) {
//
//    private var fetchExerciseJob: Job? = null
//    private val selectedLanguage = corePreferences.selectedLanguage
//
//    init {
//        fetchExerciseContent(args.exerciseId, "2000")
//    }
//
//    fun handle(action: ExerciseWordFragmentViewActions) {
//        when (action) {
//            is ExerciseWordFragmentViewActions.RequestContentRefresh -> fetchExerciseContent(
//                args.exerciseId,
//                "2000",
//                true
//            )
//        }
//    }
//
//    private fun fetchExerciseContent(
//        exerciseId: String,
//        courseId: String,
//        forceUpdate: Boolean = false,
//    ) {
//        fetchExerciseJob?.cancel()
//        fetchExerciseJob = viewModelScope.launch {
//            setState { copy(isLoading = true) }
//            learnRepo.getExerciseContents(
//                exerciseId,
//                courseId,
//                forceUpdate,
//                selectedLanguage
//            ).collect {
//                val list = it
//
//                setState { copy(isLoading = false) }
//
//                if (list != null && list.content.isNotEmpty() == true) {
//                    setState { copy(isError = false) }
//                    setState { copy(exerciseList = it.content) }
//                } else {
//                    _viewEvents.setValue(
//                        ExerciseWordFragmentViewEvents.ShowToast(
//                            stringProvider.getString(
//                                R.string.error_loading_data
//                            )
//                        )
//                    )
//
//                    setState { copy(isError = true) }
//                }
//            }
//        }
//    }
//
//    sealed class ExerciseWordFragmentViewEvents : ViewEvents {
//        class ShowToast(val toastText: String) : ExerciseWordFragmentViewEvents()
//    }
//
//    sealed class ExerciseWordFragmentViewActions : ViewModelAction {
//
//        object RequestContentRefresh : ExerciseWordFragmentViewActions()
//
//    }
//
//    data class ExerciseWordFragmentViewState(
//        val isLoading: Boolean = false,
//        val isError: Boolean = false,
//        val exerciseList: List<BaseCourseContent> = listOf()
//    ) : ViewState

}