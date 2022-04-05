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
import org.navgurukul.learn.courses.repository.LearnRepo
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
    private var classes: CourseClassContent? = null
    private var isEnrolled: Boolean = false

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
//
                        if ( Date().time > classes?.startTime?.time!!){
                            getRevisionClasses(classes!!.id)
                        }else {

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
            is ClassFragmentViewActions.RequestJoinClass -> attendClass()
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
            val revisionClasses = learnRepo.getRevisionClasses(classId)
            revisionClasses?.let {
                setState {
                    copy(
                        isLoading = true,
                        revisionClasses = it
                    )
                }
                if (it.isNotEmpty()){
                    _viewEvents.postValue(ClassFragmentViewEvents.ShowRevisionClasses(revisionClasses))
                }
            }
        }
    }


    private fun attendClass(){
        viewModelScope.launch {
            val classes = classes ?: return@launch
            if (isEnrolled) {
                val durationToClassStart = (classes.startTime.time - Date().time)
                if (classJoinEnabled(durationToClassStart)) {
                    classes.meetLink?.let { ClassFragmentViewEvents.OpenLink(it) }
                        ?.let { _viewEvents.setValue(it) }
                } else {
                    _viewEvents.setValue(
                        ClassFragmentViewEvents.ShowToast(
                            stringProvider.getString(
                               R.string.class_not_started_toast,
//                                durationToClassStart.toDisplayableInterval(stringProvider)     // Util class
                            )
                        )
                    )
                }
            } else {}
        }
    }

    private fun classJoinEnabled(durationToClassStart: Long) =
        durationToClassStart < TimeUnit.MINUTES.toMillis(5)



    sealed class ClassFragmentViewEvents : ViewEvents {
        class ShowToast(val toastText: String) : ClassFragmentViewEvents()
        data class ShowRevisionClasses(val revisionClasses: List<CourseClassContent>): ClassFragmentViewEvents()
        data class ShowClassData(val courseClass : CourseClassContent): ClassFragmentViewEvents()
        class OpenLink(val link: String) : ClassFragmentViewEvents()
    }

    sealed class ClassFragmentViewActions : ViewModelAction {
        data class MarkCompleteClicked(val classId: String) : ClassFragmentViewActions()
        object RequestContentRefresh : ClassFragmentViewActions()
        object RequestJoinClass :ClassFragmentViewActions()

    }

    data class ClassFragmentViewState(
        val isLoading: Boolean = false,
        val isError: Boolean = false,
        val classContent: CourseClassContent? = null,
        val revisionClasses: List<CourseClassContent> = arrayListOf(),

    ) : ViewState
}