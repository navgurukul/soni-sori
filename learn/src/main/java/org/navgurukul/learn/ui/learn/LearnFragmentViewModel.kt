package org.navgurukul.learn.ui.learn

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.room.Ignore
import com.google.firebase.crashlytics.FirebaseCrashlytics
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
import org.navgurukul.learn.courses.network.PathwayData
import org.navgurukul.learn.courses.network.model.Batch
import org.navgurukul.learn.courses.network.wrapper.Resource
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
              it?.run {
                    it.let {
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
                                    //shouldShowCertificate = currentPathway!!.shouldShowCertificate,
                                    showTakeTestButton = if (currentPathway!!.cta?.url?.isBlank()
                                            ?: true
                                    ) false else true
                                )
                            }
                            currentPathway?.let {
                                checkedStudentEnrolment(it.id)
                            }
                        } else {
                            setState { copy(loading = false) }
                        }
                    }
                }
            }
        }
    }

    private fun getCompletedPortion(pathwayId: Int) {
        viewModelScope.launch {
            val response = learnRepo.getCompletedPortion(pathwayId)
            when (response) {
                is Resource.Success -> {
                    response.data?.let {
                        setState {
                            copy(
                                pathwayData = it.pathway
                            )
                        }
                    }
                    response
                }
                is Resource.Error -> {
                    FirebaseCrashlytics.getInstance().recordException(Exception(response.message))
                }
                else -> {
                    FirebaseCrashlytics.getInstance().recordException(Exception(response.message))
                }
            }
        }
    }

    private fun refreshCourses(pathway: Pathway, forceUpdate: Boolean) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                checkedStudentEnrolment(pathway.id)
                learnRepo.getCoursesDataByPathway(pathway.id, forceUpdate).collect {
                    if (it != null) {
                        it.let {
                            setState { copy(courses = it, loading = false, logo = pathway.logo, shouldShowCertificate = false, code = pathway.code,
                                showTakeTestButton = if(pathway.cta?.url?.isBlank()?:true) false else true) }
                        }
                    } else{
                        _viewEvents.postValue(LearnFragmentViewEvents.ShowNetworkErrorScreen)
                    }

                }
                getCompletedPortion(pathway.id)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(Exception(e.message))
            }
        }
    }

    fun selectPathway(pathway: Pathway) {
        val selectedLanguage =
            pathway.supportedLanguages.find { it.code == corePreferences.selectedLanguage }?.label
                ?: pathway.supportedLanguages[0].label
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
        getCertificate(pathway.id, pathway.code, pathway.name)
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

            is LearnFragmentViewActions.RequestPageLoad -> {
//                checkedStudentEnrolment()
            }

            is LearnFragmentViewActions.PrimaryAction -> primaryAction(
                actions.classId,
                actions.shouldRegisterUnregisterAll
            )

            LearnFragmentViewActions.RefreshCourses -> {
                refreshCourse()
            }

            LearnFragmentViewActions.LanguageSelectionClicked -> {
                _viewEvents.postValue(LearnFragmentViewEvents.OpenLanguageSelectionSheet)
            }

            LearnFragmentViewActions.BtnMoreBatchClicked -> {
                val currentState = viewState.value!!
                _viewEvents.postValue(LearnFragmentViewEvents.OpenBatchSelectionSheet(currentState.batches))
            }

            LearnFragmentViewActions.PathwayCtaClicked -> {
                val currentState = viewState.value!!
                _viewEvents.postValue(LearnFragmentViewEvents.OpenUrl(currentState.pathways[currentState.currentPathwayIndex].cta))
            }
        }
    }

    private fun checkedStudentEnrolment(pathwayId: Int) {
        viewModelScope.launch {
            setState { copy(loading= true) }
            val status = learnRepo.checkedStudentEnrolment(pathwayId)
            when (status){
                is Resource.Success -> {
                    when(status.data?.message){
                        EnrolStatus.enrolled -> {
                            getUpcomingClasses(pathwayId)
                        }
                        EnrolStatus.not_enrolled -> {
                            getBatchesDataByPathway(pathwayId)
                        }
                        EnrolStatus.enrolled_but_finished -> {
                            getBatchesDataByPathway(pathwayId)
                            _viewEvents.postValue(LearnFragmentViewEvents.ShowCompletedStatus)
                        }
                    }
                }
                is Resource.Error -> {
                    setState { copy(loading= false) }
                    FirebaseCrashlytics.getInstance().recordException(Exception(status.message))
                }
            }
        }
    }

    private fun refreshCourse() {
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
            try{
                val batches =learnRepo.getBatchesListByPathway(pathwayId)
                when (batches){
                    is Resource.Success -> {
                        batches.data?.let {
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
                    }
                    is Resource.Error -> {
                        FirebaseCrashlytics.getInstance().recordException(Exception(batches.message))
                    }
                }
            } catch (e: Exception){
                println(e.message)
            }
        }
    }

    private fun getUpcomingClasses(pathwayId: Int) {
        viewModelScope.launch {
            val classes = learnRepo.getUpcomingClass(pathwayId)
            when (classes){
                is Resource.Success -> {
                    classes.data?.let {
                        setState {
                            copy(
                                classes = it,
                                batches = emptyList()
                            )
                        }
                        if (it.isNotEmpty()){
                            _viewEvents.postValue(LearnFragmentViewEvents.ShowUpcomingClasses(it))
                        }
                    }
                }
                is Resource.Error -> {
                    FirebaseCrashlytics.getInstance().recordException(Exception(classes.message))
                }
                else -> {
                    FirebaseCrashlytics.getInstance().recordException(Exception(classes.message))
                }
            }
        }
    }

     private fun getCertificate(pathwayId: Int, pathwayCode: String, pathwayName: String){
         viewModelScope.launch {
             val response = learnRepo.getCompletedPortion(pathwayId)
             when (response) {
                 is Resource.Success -> {
                     response.data?.let {
                         getCertificatePdf(it.totalCompletedPortion, pathwayCode, pathwayName)
                     }
                     response
                 }
                 is Resource.Error -> {
                     FirebaseCrashlytics.getInstance().recordException(Exception(response.message))
                 }
                 else -> {
                     Log.d("LearnFragmentViewModel", response.message?:"")
                     FirebaseCrashlytics.getInstance().recordException(Exception(response.message))
                 }
             }
         }
    }

    private fun getCertificatePdf(completedPortion: Int, pathwayCode: String, pathwayName: String) {
        viewModelScope.launch {
            val response = learnRepo.getCertificate(pathwayCode)
            when (response){
                is Resource.Success -> {
                    response.data?.let {
                        _viewEvents.postValue(LearnFragmentViewEvents.GetCertificate(response.data.url, completedPortion, pathwayName))
                    }
                    response
                }
                is Resource.Error -> {
                    FirebaseCrashlytics.getInstance().recordException(Exception(response.message))
                }
                else -> {
                    FirebaseCrashlytics.getInstance().recordException(Exception(response.message))
                }
            }
        }
    }

    fun selectBatch(batch: Batch) {
        _viewEvents.postValue(LearnFragmentViewEvents.BatchSelectClicked(batch))
    }

    private fun primaryAction(classId: Int, shouldRegisterUnregisterAll: Boolean = false) {
        viewModelScope.launch {
            setState { copy(loading = true) }

            try {
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
                    _viewEvents.setValue(
                        LearnFragmentViewEvents.ShowToast(
                            stringProvider.getString(
                                R.string.enroll_to_batch
                            )
                        )
                    )
                } else {
                    setState { copy(loading = false) }
                    _viewEvents.setValue(
                        LearnFragmentViewEvents.ShowToast(
                            stringProvider.getString(
                                R.string.unable_to_enroll
                            )
                        )
                    )
                }
            } catch (e: Exception) {
                setState { copy(loading = false) }
                _viewEvents.setValue(
                    LearnFragmentViewEvents.ShowToast(
                        stringProvider.getString(
                            R.string.unable_to_enroll
                        )
                    )
                )
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
    val pathwayData : List<PathwayData> = arrayListOf(),
    @Ignore
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
    class GetCertificate(val pdfUrl: String, val getCompletedPortion: Int, val pathwayName : String) : LearnFragmentViewEvents()
    object ShowNetworkErrorScreen : LearnFragmentViewEvents()
    object ShowErrorView : LearnFragmentViewEvents()
}
  sealed class LearnFragmentViewActions : ViewModelAction {
        object RequestPageLoad : LearnFragmentViewActions()
        data class PrimaryAction(val classId: Int, val shouldRegisterUnregisterAll: Boolean) :LearnFragmentViewActions()
        object ToolbarClicked : LearnFragmentViewActions()
        object BtnMoreBatchClicked : LearnFragmentViewActions()
        object LanguageSelectionClicked : LearnFragmentViewActions()
        object RefreshCourses : LearnFragmentViewActions()
        object PathwayCtaClicked : LearnFragmentViewActions()
        data class ClassPrimaryCtaClicked(val classId: String, val isEnrolled: Boolean) :
            LearnFragmentViewActions()
    }
