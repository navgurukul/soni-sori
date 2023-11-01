package org.navgurukul.learn.ui.learn.c4ca

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.merakilearn.core.utils.CorePreferences
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.ViewEvents
import org.navgurukul.commonui.platform.ViewModelAction
import org.navgurukul.commonui.platform.ViewState
import org.navgurukul.commonui.resources.StringProvider
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.BaseCourseContent
import org.navgurukul.learn.courses.db.models.CourseContentType
import org.navgurukul.learn.courses.db.models.CourseExerciseContent
import org.navgurukul.learn.courses.repository.LearnRepo
import org.navgurukul.learn.ui.learn.CourseContentArgs
import org.navgurukul.learn.ui.learn.ExerciseFragmentViewModel

class ModuleExerciseFragmentViewModel(
    private val learnRepo: LearnRepo,
    corePreferences: CorePreferences,
    private val stringProvider: StringProvider,
    private val args: CourseContentArgs
) :
    BaseViewModel<ModuleExerciseFragmentViewModel.ModuleExerciseFragmentViewEvents, ModuleExerciseFragmentViewModel.ModuleExerciseFragmentViewState>(
        ModuleExerciseFragmentViewState()
    ) {
    private var fetchExerciseJob: Job? = null
    private val selectedLanguage = corePreferences.selectedLanguage

    init {
        fetchExerciseContent(args.contentId, args.courseId, args.courseContentType)
    }

    fun handle(action: ModuleExerciseFragmentViewActions) {
        when (action) {
            is ModuleExerciseFragmentViewActions.RequestContentRefresh -> fetchExerciseContent(
                args.contentId,
                args.courseId,
                args.courseContentType,
                true
            )
        }
    }

    private fun fetchExerciseContent(
        contentId: String,
        courseId: String,
        courseContentType: CourseContentType,
        forceUpdate: Boolean = false,
    ) {
        fetchExerciseJob?.cancel()
        fetchExerciseJob = viewModelScope.launch {
            setState { copy(isLoading = true) }
            learnRepo.getCourseContentById(
                contentId,
                courseId,
                courseContentType,
                forceUpdate,
                selectedLanguage
            ).collect {
                if (it?.courseContentType == CourseContentType.exercise) {
                    val list = it as CourseExerciseContent

                    setState { copy(isLoading = false) }

                    if (list != null && list.content.isNotEmpty() == true) {
                        setState { copy(isError = false) }
                        setState { copy(exerciseContentList = list.content) }
                    } else {
                        _viewEvents.setValue(
                            ModuleExerciseFragmentViewModel.ModuleExerciseFragmentViewEvents.ShowToast(
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
    }

    sealed class ModuleExerciseFragmentViewEvents : ViewEvents {
        class ShowToast(val toastText: String) : ModuleExerciseFragmentViewEvents()
    }

    sealed class ModuleExerciseFragmentViewActions : ViewModelAction {

        object RequestContentRefresh : ModuleExerciseFragmentViewActions()

    }

    data class ModuleExerciseFragmentViewState(
        val isLoading: Boolean = false,
        val isError: Boolean = false,
        val exerciseContentList: List<BaseCourseContent> = listOf()
    ) : ViewState
}
