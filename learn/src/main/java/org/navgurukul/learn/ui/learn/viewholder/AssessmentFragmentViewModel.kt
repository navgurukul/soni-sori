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
import org.navgurukul.learn.courses.network.AttemptResponse
import org.navgurukul.learn.courses.network.AttemptStatus
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
    private var correctOutputDataList:  List<BaseCourseContent> = listOf()
    private var inCorrectOutputDataList:  List<BaseCourseContent> = listOf()


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
            is AssessmentFragmentViewActions.SubmitOptionClicked -> {
                showOutputScreen(action.selectedOptionResponse)
            }

            is AssessmentFragmentViewActions.OptionSelected ->{
                updateList(action.selectedOptionResponse, OptionViewState.SELECTED)
            }
            is AssessmentFragmentViewActions.ContentMarkCompleted -> {
                markAssessmentCompleted()
            }
        }
    }

    private fun updateList(selectedOptionResponse: OptionResponse, newViewState : OptionViewState, content: List<BaseCourseContent> ?= null) {
        val currentState = content?:viewState.value!!.assessmentContentListForUI
            currentState.forEach {
                if (it.component == BaseCourseContent.COMPONENT_OPTIONS) {
                    val optionList = it as OptionsBaseCourseContent
                    for(option in optionList.value){
                        if (option.id == selectedOptionResponse.id){
                            option.viewState = newViewState
                        }
                        else {
                            option.viewState = OptionViewState.NOT_SELECTED
                        }
                    }
                    setState { copy(assessmentContentListForUI = currentState) }
                }
            }

    }
    private suspend fun updateListAttemptStatus(assessmentId: Int, newViewState: OptionViewState){
        val selectedOption= learnRepo.getStudentResult(assessmentId).selectedOption
        val currentState = viewState.value!!
        currentState.assessmentContentListForUI.forEach {
            if (it.component == BaseCourseContent.COMPONENT_OPTIONS) {
                val optionList = it as OptionsBaseCourseContent
                for (option in optionList.value) {
                    if (option.id == selectedOption) {
                        option.viewState = newViewState
                    } else {
                        option.viewState = OptionViewState.NOT_SELECTED
                    }
                }
                setState { copy(assessmentContentListForUI = currentState.assessmentContentListForUI) }
            }
        }
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

                        allAssessmentContentList = list.content

                        list.attemptStatus.selectedOption?.let{
                            val contentListForUI = getAssessmentListForUI(list.content)
                            getOptionItemById(it, contentListForUI)?.let { option ->
                                showOutputScreen(option, contentListForUI)
                                markAssessmentCompleted()
                            }
                        }?: kotlin.run {
                            //not attempted condition
                            setState { copy(assessmentContentListForUI = getAssessmentListForUI(list.content)) }
                        }

                        val solutionList = allAssessmentContentList.find { it.component == BaseCourseContent.COMPONENT_OUTPUT }
                        solutionList?.let {
                            try {
                                it as OutputBaseCourseContent
                                inCorrectOutputDataList = it.value.incorrect
                                correctOutputDataList = it.value.correct
                            }catch (e: Exception){

                            }
                        }
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

    private fun getOptionItemById(id: Int, content: List<BaseCourseContent>): OptionResponse? {
        return content.find {
            it.component == BaseCourseContent.COMPONENT_OPTIONS
        }?.let {
            it as OptionsBaseCourseContent
            it.value.find {
                return@find it.id == id
            }
        }
    }

    private fun markAssessmentCompleted(){
        markCourseAssessmentCompleted(args.contentId.toInt())
    }

    private fun markCourseAssessmentCompleted(
       assessmentId: Int
    ) {
        viewModelScope.launch {
            learnRepo.markCourseAssessmentCompleted(assessmentId.toString())
        }
    }

    private fun postStudentResult(assessmentId: Int, status : Status, selectedOption: Int?){
        viewModelScope.launch {
            learnRepo.postStudentResult(assessmentId, status, selectedOption)
        }
    }

    private fun getAssessmentListForUI(content: List<BaseCourseContent>): List<BaseCourseContent>{
        return content.filterNot { it.component == COMPONENT_SOLUTION ||
                it.component == COMPONENT_OUTPUT }
    }

    private fun showOutputScreen(clickedOption: OptionResponse, content: List<BaseCourseContent>? = null){
        if (isOptionSelectedCorrect(clickedOption)){
            updateList(clickedOption, OptionViewState.CORRECT, content)
            postStudentResult(args.contentId.toInt(), Status.Pass,clickedOption.id)
            _viewEvents.postValue(AssessmentFragmentViewEvents.ShowCorrectOutput(correctOutputDataList))
        }else{
            updateList(clickedOption, OptionViewState.INCORRECT, content)
            postStudentResult(args.contentId.toInt(), Status.Fail,clickedOption.id)
            _viewEvents.postValue(AssessmentFragmentViewEvents.ShowIncorrectOutput(inCorrectOutputDataList))
        }
    }

    private fun isOptionSelectedCorrect(
        clickedOption: OptionResponse
    ): Boolean {
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
        data class SubmitOptionClicked(val selectedOptionResponse: OptionResponse): AssessmentFragmentViewActions()
        data class OptionSelected(val selectedOptionResponse: OptionResponse): AssessmentFragmentViewActions()
        object ContentMarkCompleted : AssessmentFragmentViewActions()
    }

    data class AssessmentFragmentViewState(
        val isLoading: Boolean = false,
        val isError: Boolean = false,
        val assessmentContentListForUI: List<BaseCourseContent> = listOf(),
        ) : ViewState
}



