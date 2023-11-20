package org.navgurukul.learn.ui.learn.viewholder

import android.annotation.SuppressLint
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
    private var selectedOption: List<Int> = listOf()


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
                postResultOnSubmit(action.selectedOptionResponse)
                showOutputScreen(action.selectedOptionResponse)
            }
//            is AssessmentFragmentViewActions.SeeExplanationClicked -> {
//                postResultOnSubmit(action.selectedOptionResponse)
//                showCorrectOnIncorrect()
//            }

            is AssessmentFragmentViewActions.OptionSelected ->{
                updateList(action.selectedOptionResponse, OptionViewState.SELECTED)
            }
            is AssessmentFragmentViewActions.ContentMarkCompleted -> {
                markAssessmentCompleted()
            }
            is AssessmentFragmentViewActions.ShowUpdatedOutput -> {
                resetList()
            }
            is AssessmentFragmentViewActions.ShowCorrectOnIncorrect -> {
                showCorrectOnIncorrect()
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun updateList(selectedOptionResponse: List<OptionResponse>, newViewState : OptionViewState, content: List<BaseCourseContent> ?= null) {
        val currentStateList = content?:viewState.value!!.assessmentContentListForUI
            currentStateList.forEach {
                if (it.component == BaseCourseContent.COMPONENT_OPTIONS) {
                    val optionList = it as OptionsBaseCourseContent
                    for(option in optionList.value){
                        if (option.id == selectedOptionResponse[0].id){
                            option.viewState = newViewState
                        }
                        else {
                            option.viewState = OptionViewState.NOT_SELECTED
                        }
                    }
                }
            }

        setState {
            copy(assessmentContentListForUI = currentStateList)
        }
        updateListInLocalDb(currentStateList)
    }

    private fun showCorrectOnIncorrect(){
        viewModelScope.launch {
            val correctOption = (allAssessmentContentList
                .find { it.component == BaseCourseContent.COMPONENT_SOLUTION } as SolutionBaseCourseContent)
                .correct_options_value[1].value
            val currentState = viewState.value!!
            currentState.assessmentContentListForUI.forEach {
                if (it.component == BaseCourseContent.COMPONENT_OPTIONS){
                    val optionList = it as OptionsBaseCourseContent
                    for (option in optionList.value){
//                        if (option.id == correctOption){
//                            option.viewState = OptionViewState.CORRECT
//                        } else if (option.id == selectedOption){                                                                      // Need to check after during implementation
//                            option.viewState = OptionViewState.INCORRECT
//                        }else {
//                            option.viewState = OptionViewState.NOT_SELECTED
//                        }
                    }
                }
            }

            setState {
                copy(assessmentContentListForUI = currentState.assessmentContentListForUI)
            }
        }

    }

    private fun updateListInLocalDb(currentStateList: List<BaseCourseContent>) {
        viewModelScope.launch {
            learnRepo.updateAssessmentListInLocalDb(currentStateList)
        }
    }

    private fun updateListAttemptStatus(optionSelected: List<Int>?, assessmentId: Int, newViewState: OptionViewState){
        if (optionSelected != null) {
            selectedOption = optionSelected
        }
        val currentState = viewState.value!!
        currentState.assessmentContentListForUI.forEach {
            if (it.component == BaseCourseContent.COMPONENT_OPTIONS) {
                val optionList = it as OptionsBaseCourseContent
                for (option in optionList.value) {
//                    if (option.id == selectedOption) {
//                        option.viewState = newViewState
//                    } else {
//                        option.viewState = OptionViewState.NOT_SELECTED
//                    }
                }
            }
        }
        setState { copy(assessmentContentListForUI = currentState.assessmentContentListForUI) }
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

                        val solutionList = allAssessmentContentList.find { it.component == BaseCourseContent.COMPONENT_OUTPUT }
                        solutionList?.let {
                            try {
                                it as OutputBaseCourseContent
                                inCorrectOutputDataList = it.value.incorrect
                                correctOutputDataList = it.value.correct
                            }catch (e: Exception){

                            }
                        }

                        val selOption = list.attemptStatus?.selectedOption
                        if(!selOption.isNullOrEmpty()){
                            val contentListForUI = getAssessmentListForUI(list.content)
                            getOptionItemById(selOption[0], contentListForUI)?.let {
//                                showOutputScreen(option, contentListForUI)
                            }
                        } else {
                            //not attempted condition
                            setState { copy(assessmentContentListForUI = getAssessmentListForUI(list.content)) }
                        }

//                        list.attemptStatus?.selectedOption?.let{
//                            val contentListForUI = getAssessmentListForUI(list.content)
//                            getOptionItemById(it[1], contentListForUI)?.let { option ->      // comment for the changes new assessment unused checked above updaated code
//                                showOutputScreen(option, contentListForUI)
//                            }
//                        }?: kotlin.run {
//                            //not attempted condition
//                            setState { copy(assessmentContentListForUI = getAssessmentListForUI(list.content)) }
//                        }

//                        getAttemptStatus(list.id.toInt())                      // commenting to stop to getassessment api call call

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
//        markCourseAssessmentCompleted(args.contentId.toInt())
    }

    private fun postStudentResult(assessmentId: Int, status : Status, selectedOptions: List<Int>){
        viewModelScope.launch {
            learnRepo.postStudentResult(assessmentId, status, selectedOptions)
        }
    }

//    private fun getAttemptStatus(assessmentId: Int){
//        viewModelScope.launch {
//            setState { copy(isLoading = false) }
//            val attemptResponse = learnRepo.getStudentResult(assessmentId)
//            val attemptStatus = attemptResponse.attemptStatus
//            if (attemptStatus == AttemptStatus.CORRECT){
//                updateListAttemptStatus(attemptResponse.selectedOption, assessmentId, OptionViewState.CORRECT)
//                _viewEvents.postValue(AssessmentFragmentViewEvents.ShowCorrectOutput(correctOutputDataList))
//            } else if ( attemptStatus == AttemptStatus.INCORRECT){
//                updateListAttemptStatus(attemptResponse.selectedOption, assessmentId, OptionViewState.INCORRECT)
//                _viewEvents.postValue(AssessmentFragmentViewEvents.ShowRetryOnce(inCorrectOutputDataList, attemptResponse))
//            }
//        }
//    }

    private fun resetList(){
        viewState.value?.assessmentContentListForUI?.forEach {
            if(it.component == BaseCourseContent.COMPONENT_OPTIONS){
                val item = it as OptionsBaseCourseContent
                item.value = item.value.toMutableList().map{ it.copy(viewState = OptionViewState.NOT_SELECTED) }
            }
        }
        setState { copy(assessmentContentListForUI = assessmentContentListForUI) }
    }


    private fun getAssessmentListForUI(content: List<BaseCourseContent>): List<BaseCourseContent>{
        return content.filterNot {
            it.component == COMPONENT_SOLUTION || it.component == COMPONENT_OUTPUT
        }
    }

    private fun showOutputScreen(clickedOption: List<OptionResponse>, content: List<BaseCourseContent>? = null){
        if (isOptionSelectedCorrect(clickedOption)){
            updateList(clickedOption, OptionViewState.CORRECT, content)
            _viewEvents.postValue(AssessmentFragmentViewEvents.ShowCorrectOutput(correctOutputDataList))
        }else{
            updateList(clickedOption, OptionViewState.INCORRECT, content)
            _viewEvents.postValue(AssessmentFragmentViewEvents.ShowIncorrectOutput(inCorrectOutputDataList))
        }
    }

    private fun postResultOnSubmit(clickedOption: List<OptionResponse>){
        val int :Int = clickedOption[0].id
        if (isOptionSelectedCorrect(clickedOption)){
            postStudentResult(args.contentId.toInt(), Status.Pass, listOf(int) )
        }
        else{
            postStudentResult(args.contentId.toInt(), Status.Fail, listOf(int) )
        }
    }

    private fun isOptionSelectedCorrect(
        clickedOption: List<OptionResponse>
    ): Boolean {
        try {
            return clickedOption[0].id ==
                    (allAssessmentContentList
                        .find { it.component == BaseCourseContent.COMPONENT_SOLUTION } as SolutionBaseCourseContent)
                        .correct_options_value[1].value

        }catch (e: Exception){
            return false
        }
    }

    sealed class AssessmentFragmentViewEvents : ViewEvents {
        class ShowToast(val toastText: String) : AssessmentFragmentViewModel.AssessmentFragmentViewEvents()
        data class ShowCorrectOutput(val list : List<BaseCourseContent>): AssessmentFragmentViewEvents()
        data class ShowIncorrectOutput(val list : List<BaseCourseContent>) : AssessmentFragmentViewEvents()
        data class ShowPartiallyCorrectOutput(val list: List<BaseCourseContent>):AssessmentFragmentViewEvents()
        data class ShowPartiallyIncorrectOutput(val list: List<BaseCourseContent>):AssessmentFragmentViewEvents()
        data class ShowRetryOnce(val list : List<BaseCourseContent>, val attemptResponse: AttemptResponse)  : AssessmentFragmentViewEvents()
    }


    sealed class AssessmentFragmentViewActions : ViewModelAction {
        object RequestContentRefresh : AssessmentFragmentViewActions()
        data class SubmitOptionClicked(val selectedOptionResponse: List<OptionResponse>): AssessmentFragmentViewActions()
        data class OptionSelected(val selectedOptionResponse: List<OptionResponse>): AssessmentFragmentViewActions()
//        data class SeeExplanationClicked(val selectedOptionResponse: OptionResponse): AssessmentFragmentViewActions()
        object ShowUpdatedOutput : AssessmentFragmentViewActions()
        object ShowCorrectOnIncorrect : AssessmentFragmentViewActions()
        object ContentMarkCompleted : AssessmentFragmentViewActions()
    }

    data class AssessmentFragmentViewState(
        val isLoading: Boolean = false,
        val isError: Boolean = false,
        val assessmentContentListForUI: List<BaseCourseContent> = listOf(),
        ) : ViewState
}
