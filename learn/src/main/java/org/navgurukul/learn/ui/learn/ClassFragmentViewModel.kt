package org.navgurukul.learn.ui.learn

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
import org.navgurukul.learn.courses.db.models.CourseClassContent
import org.navgurukul.learn.courses.db.models.CourseContentType
import org.navgurukul.learn.courses.network.EnrolResponse
import org.navgurukul.learn.courses.network.EnrolStatus
import org.navgurukul.learn.courses.network.model.Batch
import org.navgurukul.learn.courses.repository.LearnRepo
import org.navgurukul.learn.util.toDisplayableInterval
import java.util.*
import java.util.concurrent.TimeUnit

class ClassFragmentViewModel(
    private val learnRepo: LearnRepo,
    corePreferences: CorePreferences,
    private val stringProvider: StringProvider,
    private val args: CourseContentArgs
): BaseViewModel<ClassFragmentViewModel.ClassFragmentViewEvents, ClassFragmentViewModel.ClassFragmentViewState>(
    ClassFragmentViewState()
) {

    private var fetchClassJob: Job? = null
    private val selectedLanguage = corePreferences.selectedLanguage

    init {
        fetchClassContent(args.contentId, args.courseId, args.courseContentType)
    }

    private fun fetchClassContent(
        contentId: String,
        courseId: String,
        courseContentType: CourseContentType,
        forceUpdate: Boolean = false,
    ) {
        fetchClassJob?.cancel()
        fetchClassJob = viewModelScope.launch {
            setState { copy(isLoading = true) }
            learnRepo.getCourseContentById(
                contentId,
                courseId,
                courseContentType,
                forceUpdate,
                selectedLanguage
            ).collect {
                if(it?.courseContentType == CourseContentType.class_topic) {
                    val data = it as CourseClassContent

                    setState { copy(isLoading = false) }

                    if (data != null) {
                        setState { copy(isError = false) }
                        setState { copy(classContent = data) }

                        val status = learnRepo.statusEnrolled?.message
                        if ( status == EnrolStatus.enrolled){
                            if (Date().time > data.endTime.time){
                                getRevisionClasses(data.id)
                            }else{
                               _viewEvents.postValue(ClassFragmentViewEvents.ShowClassData(data))
                            }
                        }
                        else{
                            getBatchesDataByPathway(1)
                        }

                    } else {
                        _viewEvents.setValue(
                            ClassFragmentViewEvents.ShowToast(
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
    
    fun handle(action: ClassFragmentViewModel.ClassFragmentViewActions) {
        when (action) {
            is ClassFragmentViewActions.MarkCompleteClicked -> markCourseClassCompleted(action.classId)
            is ClassFragmentViewActions.RequestContentRefresh -> fetchClassContent(
                args.contentId,
                args.courseId,
                args.courseContentType,
                true
            )
        }
    }

    private fun markCourseClassCompleted(
        classId: String
    ) {
        viewModelScope.launch {
            learnRepo.markCourseClassCompleted(classId)
        }
    }

    private fun getRevisionClasses(classId: String){
        viewModelScope.launch {
            setState {copy(isLoading = true)}

            val revisionClasses = learnRepo.getRevisionClasses(classId)
            revisionClasses?.let {
                setState {
                    copy(
                        isLoading = false,
                        revisionClasses = it
                    )
                }
                if (it.isNotEmpty()){
                    if(it.first().isEnrolled)
                        _viewEvents.postValue(ClassFragmentViewEvents.ShowRevisionClassToJoin(it.first()))
                    else
                        _viewEvents.postValue(ClassFragmentViewEvents.ShowRevisionClasses(it))
                }else{
                    _viewEvents.postValue(ClassFragmentViewEvents.ShowRevisionClasses(it))
                }
            }
        }
    }

    private fun getBatchesDataByPathway(pathwayId: Int) {
        viewModelScope.launch {
            setState { copy(isLoading=true) }
            val batches =learnRepo.getBatchesListByPathway(pathwayId)
            batches?.let {
                setState {
                    copy(
                        batches = it
                    )
                }
                if (it.isNotEmpty()){
                    _viewEvents.postValue(ClassFragmentViewEvents.ShowBatches(batches))
                }
            }
        }
    }

    sealed class ClassFragmentViewEvents : ViewEvents {
        class ShowToast(val toastText: String) : ClassFragmentViewEvents()
        data class ShowRevisionClassToJoin(val revisionClass: CourseClassContent): ClassFragmentViewEvents()
        data class ShowRevisionClasses(val revisionClasses: List<CourseClassContent>): ClassFragmentViewEvents()
        data class ShowClassData(val courseClass : CourseClassContent): ClassFragmentViewEvents()
        data class ShowBatches(val batches : List<Batch>):ClassFragmentViewEvents()
        class OpenLink(val link: String) : ClassFragmentViewEvents()
    }

    sealed class ClassFragmentViewActions : ViewModelAction {
        data class MarkCompleteClicked(val classId: String) : ClassFragmentViewActions()
        object RequestContentRefresh : ClassFragmentViewActions()
    }

    data class ClassFragmentViewState(
        val isLoading: Boolean = false,
        val isError: Boolean = false,
        val classContent: CourseClassContent? = null,
        val revisionClasses: List<CourseClassContent> = arrayListOf(),
        val batches: List<Batch> = arrayListOf()

    ) : ViewState
}