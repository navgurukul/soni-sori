package org.navgurukul.learn.ui.learn.viewholder

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
import org.navgurukul.learn.courses.db.models.*
import org.navgurukul.learn.courses.db.models.BaseCourseContent.Companion.COMPONENT_OUTPUT
import org.navgurukul.learn.courses.db.models.BaseCourseContent.Companion.COMPONENT_SOLUTION
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
                updateList(action.selectedOptionResponse)
                showOutputScreen(action.selectedOptionResponse)
            }
        }
    }

    private fun updateList(selectedOptionResponse: OptionResponse) {
        val currentState = viewState.value!!
            currentState.assessmentContentList.forEach {
                if (it.component == BaseCourseContent.COMPONENT_OPTIONS) {
                    val option = it as OptionsBaseCourseContent
                    for(item in option.value){
                        if (item == selectedOptionResponse){
                            item.viewState = OptionViewState.SELECTED
                            break
                        }
                    }
                }
            }
        setState { copy(assessmentContentList = currentState.assessmentContentList) }
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
                    val list1 = it as OutputBaseCourseContent

                    setState { copy(isLoading = false) }

                    if (list != null && list.content.isNotEmpty() == true){
                        setState { copy(isError = false) }

                        setState { copy(assessmentContentList = getAssessmentListForUI(list.content)) }
                        setState { copy(correctOutput = getOutputListForUI(list1.value.correct) ) }
                        setState { copy(incorrectOutput = getOutputListForUI(list1.value.incorrect)) }


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

    private fun getOutputListForUI(content: List<BaseCourseContent> ): List<BaseCourseContent>{
        return content.filter {it.component == COMPONENT_OUTPUT}
    }

    private fun getAssessmentListForUI(content: List<BaseCourseContent>): List<BaseCourseContent>{
        return content.filterNot { it.component == COMPONENT_SOLUTION ||
                it.component == COMPONENT_OUTPUT }
    }

//    fun showOutputScreen(clickedOption:OptionResponse){
//        val currentState = viewState.value!!
//        if (isOptionSelectedCorrect(currentState, clickedOption)){
//            if(clickedOption.id == (currentState.assessmentContentList.find {it.component == BaseCourseContent.COMPONENT_SOLUTION } as SolutionBaseCourseContent).value){
//                clickedOption.viewState = OptionViewState.CORRECT
//                _viewEvents.postValue(AssessmentFragmentViewEvents.ShowCorrectOutput(currentState.correctOutput))
//            } else{
//                clickedOption.viewState = OptionViewState.INCORRECT
//                _viewEvents.postValue(AssessmentFragmentViewEvents.ShowIncorrectOutput(currentState.incorrectOutput))
//                }
//        } else{
//            _viewEvents.postValue(AssessmentFragmentViewEvents.ShowCorrectOutput(currentState.correctOutput))
//
//        }
//    }

    fun showOutputScreen(clickedOption: OptionResponse){
        val currentState = viewState.value!!
        if (isOptionSelectedCorrect(currentState, clickedOption)){
            clickedOption.viewState = OptionViewState.CORRECT
            setState { copy(correctOutput = currentState.correctOutput) }
            _viewEvents.postValue(AssessmentFragmentViewEvents.ShowCorrectOutput(currentState.correctOutput))
        }else{
            clickedOption.viewState = OptionViewState.INCORRECT
            setState { copy(incorrectOutput = currentState.incorrectOutput) }
            _viewEvents.postValue(AssessmentFragmentViewEvents.ShowIncorrectOutput(currentState.incorrectOutput))
        }
    }

    private fun isOptionSelectedCorrect(
        currentState: AssessmentFragmentViewState,
        clickedOption: OptionResponse
    ): Boolean {
        try {
            clickedOption.viewState = OptionViewState.SELECTED
            return clickedOption.id ==
                    (currentState.assessmentContentList
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
    }

    data class AssessmentFragmentViewState(
        val isLoading: Boolean = false,
        val isError: Boolean = false,
        val assessmentContentList: List<BaseCourseContent> = listOf(),
        val correctOutput: List<BaseCourseContent> =  arrayListOf(),
        val incorrectOutput: List<BaseCourseContent> =  arrayListOf(),

    ) : ViewState
}



