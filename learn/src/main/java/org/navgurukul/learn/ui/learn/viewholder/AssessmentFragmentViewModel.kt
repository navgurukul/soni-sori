package org.navgurukul.learn.ui.learn.viewholder

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.merakilearn.core.utils.CorePreferences
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.ViewEvents
import org.navgurukul.commonui.platform.ViewModelAction
import org.navgurukul.commonui.platform.ViewState
import org.navgurukul.commonui.resources.StringProvider
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.*
import org.navgurukul.learn.courses.db.models.BaseCourseContent.Companion.COMPONENT_OUTPUT
import org.navgurukul.learn.courses.db.models.BaseCourseContent.Companion.COMPONENT_SOLUTION
import org.navgurukul.learn.courses.network.Status
import org.navgurukul.learn.courses.repository.LearnRepo
import org.navgurukul.learn.ui.learn.CourseContentArgs

class AssessmentFragmentViewModel (
    private val learnRepo: LearnRepo,
    corePreferences: CorePreferences,
    private val stringProvider: StringProvider,
    private val args: CourseContentArgs
) :
    BaseViewModel<AssessmentFragmentViewModel.AssessmentFragmentViewEvents, AssessmentFragmentViewModel.AssessmentFragmentViewState>(
        AssessmentFragmentViewModel.AssessmentFragmentViewState()
    )
{
    private var fetchAssessmentJob : Job? = null
    private val selectedLanguage = corePreferences.selectedLanguage
    private var allAssessmentContentList:  List<BaseCourseContent> = listOf()
    private var outputContentList : List<BaseCourseContent> = listOf()

    init {
        fetchAssessmentContent(args.contentId,args.courseId,args.courseContentType)
    }

    fun handle(action: AssessmentFragmentViewModel.AssessmentFragmentViewActions) {
        when (action) {
            is AssessmentFragmentViewModel.AssessmentFragmentViewActions.RequestContentRefresh -> fetchAssessmentContent(
                args.contentId,
                args.courseId,
                args.courseContentType,
                true
            )
            is AssessmentFragmentViewActions.OptionSelectedClicked -> {
                showOutputScreen(action.selectedOptionResponse)
                updateList(action.selectedOptionResponse, OptionViewState.SELECTED )
            }
            is AssessmentFragmentViewActions.ShowUpdatedOutput ->{
                resetList()
            }
        }
    }

    private fun updateList(selectedOptionResponse: OptionResponse, selectedViewState : OptionViewState) {
        val currentState = viewState.value!!
            currentState.assessmentContentListForUI.forEach {
                if (it.component == BaseCourseContent.COMPONENT_OPTIONS) {
                    val option = it as OptionsBaseCourseContent
                    for(item in option.value){
                        if (item == selectedOptionResponse){
                            item.viewState = selectedViewState
                            break
                        }
                    }
                }
            }
        setState { copy(assessmentContentListForUI = currentState.assessmentContentListForUI) }
    }

    private fun resetList(): MutableList<BaseCourseContent>? {
        val currentState = viewState.value!!
        val newList = viewState.value?.assessmentContentListForUI?.toMutableList()
        newList?.forEach {
            if(it.component == BaseCourseContent.COMPONENT_OPTIONS){
                val item = it as OptionsBaseCourseContent
                item.value = item.value.toMutableList().map{ it.copy(viewState = OptionViewState.NOT_SELECTED) }
            }
        }
        setState { copy(assessmentContentListForUI = currentState.assessmentContentListForUI) }
        return newList
    }


    private fun fetchAssessmentContent(
        contentId: String,
        courseId: String,
        courseContentType: CourseContentType,
        forceUpdate: Boolean = false,
    ) {
        fetchAssessmentJob?.cancel()
        fetchAssessmentJob = viewModelScope.launch {
            setState { copy(isLoading = true) }
            learnRepo.getCourseContentById(
                contentId,
                courseId,
                courseContentType,
                forceUpdate,
                selectedLanguage
            ).collect {
                if (it?.courseContentType == CourseContentType.assessment){
                    val list = it as CourseAssessmentContent

                    setState { copy(isLoading = false) }

                    if (list != null && list.content.isNotEmpty() == true){
                        setState { copy(isError = false) }

                        setState { copy(assessmentContentListForUI = getAssessmentListForUI(list.content)) }
                        allAssessmentContentList = list.content
                    } else {
                        _viewEvents.setValue(
                            AssessmentFragmentViewEvents.ShowToast(
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

    private fun postStudentResult(assessmentId: Int , status : Status){
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            learnRepo.postStudentResult(assessmentId, status)
        }
    }

    private fun getAssessmentListForUI(content: List<BaseCourseContent>): List<BaseCourseContent>{
        resetList()
        return content.filterNot { it.component == COMPONENT_SOLUTION ||
                it.component == COMPONENT_OUTPUT }
    }

    fun showOutputScreen(clickedOption: OptionResponse){
        val currentState = viewState.value!!
        if (isOptionSelectedCorrect(currentState, clickedOption)){
            updateList(clickedOption, OptionViewState.CORRECT )
            outputContentList = (allAssessmentContentList.find { it.component == COMPONENT_OUTPUT } as OutputBaseCourseContent).value.correct
            _viewEvents.postValue(AssessmentFragmentViewEvents.ShowCorrectOutput(outputContentList))
        }else{
            updateList(clickedOption, OptionViewState.INCORRECT )
            outputContentList = (allAssessmentContentList.find { it.component == COMPONENT_OUTPUT } as OutputBaseCourseContent).value.incorrect
            _viewEvents.postValue(AssessmentFragmentViewEvents.ShowIncorrectOutput(outputContentList))
        }
    }

    private fun isOptionSelectedCorrect(
        currentState: AssessmentFragmentViewState,
        clickedOption: OptionResponse
    ): Boolean {
        Log.d("clickedOption", "${clickedOption.id}")
        try {
            return clickedOption.id ==
                    (allAssessmentContentList
                        .find { it.component == BaseCourseContent.COMPONENT_SOLUTION } as SolutionBaseCourseContent)
                        .value

        }catch (e: Exception){
            return false
        }
    }

    sealed class AssessmentFragmentViewEvents : ViewEvents {
        class ShowToast(val toastText: String) : AssessmentFragmentViewModel.AssessmentFragmentViewEvents()
        data class ShowCorrectOutput(val list : List<BaseCourseContent>): AssessmentFragmentViewEvents()
        data class ShowIncorrectOutput(val list : List<BaseCourseContent>) : AssessmentFragmentViewEvents()
    }


    sealed class AssessmentFragmentViewActions : ViewModelAction {
        object RequestContentRefresh : AssessmentFragmentViewActions()
        data class OptionSelectedClicked(val selectedOptionResponse: OptionResponse): AssessmentFragmentViewActions()
        object ShowUpdatedOutput : AssessmentFragmentViewActions()
    }

    data class AssessmentFragmentViewState(
        val isLoading: Boolean = false,
        val isError: Boolean = false,
        val assessmentContentListForUI: List<BaseCourseContent> = listOf(),
        ) : ViewState
}



