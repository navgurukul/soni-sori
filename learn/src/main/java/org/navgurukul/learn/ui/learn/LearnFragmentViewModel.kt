package org.navgurukul.learn.ui.learn

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.merakilearn.core.datasource.model.Language
import org.merakilearn.core.utils.CorePreferences
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.ViewEvents
import org.navgurukul.commonui.platform.ViewModelAction
import org.navgurukul.commonui.platform.ViewState
import org.navgurukul.commonui.resources.StringProvider
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.Course
import org.navgurukul.learn.courses.db.models.CourseClassContent
import org.navgurukul.learn.courses.db.models.Pathway
import org.navgurukul.learn.courses.db.models.PathwayCTA
import org.navgurukul.learn.courses.network.EnrolStatus
import org.navgurukul.learn.courses.network.model.Batch
import org.navgurukul.learn.courses.repository.LearnRepo

class LearnFragmentViewModel(
    private val learnRepo: LearnRepo,
    private val corePreferences: CorePreferences,
    private val stringProvider: StringProvider,

) :
    BaseViewModel<LearnFragmentViewEvents, LearnFragmentViewState>(LearnFragmentViewState()) {
    private var isEnrolled: Boolean = false

    init {
        setState { copy(loading = true) }
        viewModelScope.launch(Dispatchers.Default) {
            learnRepo.getPathwayData(true).collect {
                it?.let {
                    var currentPathway: Pathway? = null
                    if (it.isNotEmpty()) {
                        setState {
                            val lastSelectedPathwayId = corePreferences.lastSelectedPathWayId
                            var currentPathwayIndex = currentPathwayIndex
                            currentPathway = it[currentPathwayIndex]
                            it.forEachIndexed { index, pathway ->
                                if (pathway.id == lastSelectedPathwayId) {
                                    currentPathwayIndex = index
                                    currentPathway = pathway
                                }
                            }
                            val courses = currentPathway!!.courses

                            if (currentPathway!!.courses.isEmpty()) {
                                selectPathway(currentPathway!!)
                            }
                            val selectedLanguage =
                                currentPathway!!.supportedLanguages.find { it.code == corePreferences.selectedLanguage }?.label
                                    ?: currentPathway!!.supportedLanguages[0].label
                            copy(
                                loading = courses.isEmpty(),
                                pathways = it,
                                courses = courses,
                                currentPathwayIndex = currentPathwayIndex,
                                subtitle = currentPathway!!.name,
                                languages = currentPathway!!.supportedLanguages,
                                selectedLanguage = selectedLanguage,
                                logo = currentPathway!!.logo,
                                code = currentPathway!!.code,
                                shouldShowCertificate = currentPathway!!.shouldShowCertificate,
                                showTakeTestButton = if (currentPathway!!.cta?.url?.isBlank()
                                        ?: true
                                ) false else true
                            )
                        }
                        currentPathway?.let {
                            checkedStudentEnrolment(it.id)
                            getCertificate(it.id)
                        }
                    } else {
                        setState { copy(loading = false) }
                    }
                }
            }
        }
    }

    private fun refreshCourses(pathway: Pathway, forceUpdate: Boolean) {
        viewModelScope.launch(Dispatchers.Default) {
            checkedStudentEnrolment(pathway.id)
            learnRepo.getCoursesDataByPathway(pathway.id, forceUpdate).collect {
                it?.let {
                    setState { copy(courses = it, loading = false, logo = pathway.logo, shouldShowCertificate = false, code = pathway.code,
                        showTakeTestButton = if(pathway.cta?.url?.isBlank()?:true) false else true) }
                }
            }
        }
    }

    fun selectPathway(pathway: Pathway) {
        val selectedLanguage = pathway.supportedLanguages.find { it.code == corePreferences.selectedLanguage }?.label ?: pathway.supportedLanguages[0].label
        setState {
            copy(
                currentPathwayIndex = pathways.indexOf(pathway),
                subtitle = pathway.name,
                loading = true,
                languages = pathway.supportedLanguages,
                selectedLanguage = selectedLanguage
            )
        }
        corePreferences.lastSelectedPathWayId = pathway.id
        _viewEvents.postValue(LearnFragmentViewEvents.DismissSelectionSheet)
        refreshCourses(pathway, false)
    }


    fun selectLanguage(language: Language) {
        setState {
            copy(
                selectedLanguage = language.label
            )
        }
        corePreferences.selectedLanguage = language.code!!
        _viewEvents.postValue(LearnFragmentViewEvents.DismissSelectionSheet)
    }

    fun selectCourse(course: Course) {
        course.pathwayId?.let {
            _viewEvents.postValue(
                LearnFragmentViewEvents.OpenCourseDetailActivity(course.id, course.name, it)
            )
        }
    }

   fun handle(actions: LearnFragmentViewActions) {
        when (actions) {
            is LearnFragmentViewActions.ToolbarClicked -> {
                _viewEvents.postValue(LearnFragmentViewEvents.OpenPathwaySelectionSheet)
            }
            is LearnFragmentViewActions.RequestPageLoad ->{
//                checkedStudentEnrolment()
            }
            is LearnFragmentViewActions.PrimaryAction -> primaryAction(actions.classId, actions.shouldRegisterUnregisterAll)
            LearnFragmentViewActions.RefreshCourses -> {
               refreshCourse()
            }
            LearnFragmentViewActions.LanguageSelectionClicked -> {
                _viewEvents.postValue(LearnFragmentViewEvents.OpenLanguageSelectionSheet)
            }
            LearnFragmentViewActions.BtnMoreBatchClicked ->{
                val currentState = viewState.value!!
                _viewEvents.postValue(LearnFragmentViewEvents.OpenBatchSelectionSheet(currentState.batches))
            }
            LearnFragmentViewActions.PathwayCtaClicked -> {
                val currentState = viewState.value!!
                _viewEvents.postValue(LearnFragmentViewEvents.OpenUrl(currentState.pathways[currentState.currentPathwayIndex].cta))
            }
        }
    }

     private fun checkedStudentEnrolment(pathwayId: Int){
        viewModelScope.launch {
            setState { copy(loading=true) }
            val status = learnRepo.checkedStudentEnrolment(pathwayId)?.message
            if(status == EnrolStatus.enrolled){
                getUpcomingClasses(pathwayId)
            } else if(status == EnrolStatus.not_enrolled){
                getBatchesDataByPathway(pathwayId)
            }else if(status == EnrolStatus.enrolled_but_finished){
                getBatchesDataByPathway(pathwayId)
                _viewEvents.postValue(LearnFragmentViewEvents.ShowCompletedStatus)
            }
        }
    }

    private fun refreshCourse(){
        val currentState = viewState.value!!
        if (currentState.pathways.size > currentState.currentPathwayIndex) {
            refreshCourses(pathway = currentState.pathways[currentState.currentPathwayIndex], true)
        } else {
            //TO hide progress bar
            setState { copy() }
        }
    }

    private fun getBatchesDataByPathway(pathwayId: Int) {
        viewModelScope.launch {
            val batches =learnRepo.getBatchesListByPathway(pathwayId)
            batches?.let {
                setState {
                    copy(
                        batches = it,
                        classes = emptyList()
                    )
                }
                if(it.isNotEmpty()){
                    _viewEvents.postValue(LearnFragmentViewEvents.ShowUpcomingBatch(it[0]))
                }
            }
            setState { copy(loading = false) }
        }
    }

    private fun getUpcomingClasses(pathwayId: Int){
        viewModelScope.launch {
            val classes = learnRepo.getUpcomingClass(pathwayId)
            classes.let {
                setState {
                    copy(
                        classes = it,
                        batches = emptyList()
                    )
                }
                if (it.isNotEmpty()){
                    _viewEvents.postValue(LearnFragmentViewEvents.ShowUpcomingClasses(classes))
                }
            }
            setState { copy(loading = false) }
        }
    }

    private fun getCertificate(pathwayId: Int){
        viewModelScope.launch {
            val completedData = learnRepo.getCompletedPortion(pathwayId).totalCompletedPortion
            getCertificatePdf(completedData)
        }
    }

    private fun getCertificatePdf(completedPortion: Int){
        viewModelScope.launch {
            try {
                val certificatePdfUrl = learnRepo.getCertificate().url
                println("certificateUrl $certificatePdfUrl")
                _viewEvents.postValue(LearnFragmentViewEvents.GetCertificate(certificatePdfUrl, completedPortion))
            }catch (e : Exception){
                e.printStackTrace()
            }
        }
    }
    fun selectBatch(batch: Batch) {
        _viewEvents.postValue(LearnFragmentViewEvents.BatchSelectClicked(batch))
    }

    private fun primaryAction(classId: Int, shouldRegisterUnregisterAll: Boolean = false) {
        viewModelScope.launch {
        setState { copy(loading = true) }
        val result = learnRepo.enrollToClass(classId, false, shouldRegisterUnregisterAll)
        if (result) {
            isEnrolled = true
            setState {
                copy(
                    loading = false,
                )
            }
            refreshCourse()
            _viewEvents.postValue(LearnFragmentViewEvents.EnrolledSuccessfully)
            _viewEvents.setValue(LearnFragmentViewEvents.ShowToast(stringProvider.getString(R.string.enroll_to_batch)))
        } else {
            setState { copy(loading = false) }
            _viewEvents.setValue(LearnFragmentViewEvents.ShowToast(stringProvider.getString(R.string.unable_to_enroll)))
        }
        }
    }
    }

data class LearnFragmentViewState(
    val loading: Boolean = false,
    val subtitle: String? = null,
    val pathways: List<Pathway> = arrayListOf(),
    val batches: List<Batch> = arrayListOf(),
    val classes: List<CourseClassContent> = arrayListOf(),
    val currentPathwayIndex: Int = 0,
    val courses: List<Course> = arrayListOf(),
    val selectedLanguage: String? = null,
    val languages: List<Language> = arrayListOf(),
    val logo: String? = null,
    val code : String ? = null,
    val showTakeTestButton: Boolean = false,
    val menuId: Int? = null,
    val classId: Int = 0,
    var shouldShowCertificate: Boolean = false
) : ViewState

sealed class LearnFragmentViewEvents : ViewEvents {
    class ShowToast(val toastText: String) :LearnFragmentViewEvents()
    data class OpenBatchSelectionSheet(val batches: List<Batch>):LearnFragmentViewEvents()
    data class ShowUpcomingBatch(val batch: Batch):LearnFragmentViewEvents()
    data class ShowUpcomingClasses(val classes: List<CourseClassContent>) : LearnFragmentViewEvents()
    object ShowCompletedStatus : LearnFragmentViewEvents()
    object OpenPathwaySelectionSheet : LearnFragmentViewEvents()
    object OpenLanguageSelectionSheet : LearnFragmentViewEvents()
    data class BatchSelectClicked(val batch: Batch) : LearnFragmentViewEvents()
    object DismissSelectionSheet : LearnFragmentViewEvents()
    class OpenCourseDetailActivity(val courseId: String, val courseName: String, val pathwayId: Int) :
        LearnFragmentViewEvents()
    data class OpenUrl(val cta: PathwayCTA?) : LearnFragmentViewEvents()
    object EnrolledSuccessfully : LearnFragmentViewEvents()
    class GetCertificate(val pdfUrl: String, val getCompletedPortion: Int) : LearnFragmentViewEvents()
}

sealed class LearnFragmentViewActions : ViewModelAction {
    object RequestPageLoad :LearnFragmentViewActions()
    data class PrimaryAction(val classId: Int, val shouldRegisterUnregisterAll: Boolean) : LearnFragmentViewActions()
    object ToolbarClicked : LearnFragmentViewActions()
    object BtnMoreBatchClicked: LearnFragmentViewActions()
    object LanguageSelectionClicked : LearnFragmentViewActions()
    object RefreshCourses : LearnFragmentViewActions()
    object PathwayCtaClicked : LearnFragmentViewActions()
    data class ClassPrimaryCtaClicked(val classId: String, val isEnrolled: Boolean) : LearnFragmentViewActions()
}
