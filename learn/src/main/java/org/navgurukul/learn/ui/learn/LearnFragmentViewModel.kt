package org.navgurukul.learn.ui.learn


import android.view.View
import androidx.lifecycle.MutableLiveData
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
import org.navgurukul.learn.courses.db.models.Pathway
import org.navgurukul.learn.courses.db.models.PathwayCTA
import org.navgurukul.learn.courses.network.EnrolStatus
import org.navgurukul.learn.courses.network.model.Batch
import org.navgurukul.learn.courses.network.model.UpcomingClass
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
                    if (it.isNotEmpty()) {
                        setState {
                            val lastSelectedPathwayId = corePreferences.lastSelectedPathWayId
                            var currentPathwayIndex = currentPathwayIndex
                            var currentPathway = it[currentPathwayIndex]
                            it.forEachIndexed { index, pathway ->
                                if (pathway.id == lastSelectedPathwayId) {
                                    currentPathwayIndex = index
                                    currentPathway = pathway
                                }
                            }
                            val courses = currentPathway.courses

                            if (currentPathway.courses.isEmpty()) {
                                selectPathway(currentPathway)
                            }
                            val selectedLanguage = currentPathway.supportedLanguages.find { it.code == corePreferences.selectedLanguage }?.label ?:  currentPathway.supportedLanguages[0].label
                            copy(
                                loading = courses.isEmpty(),
                                pathways = it,
                                courses = courses,
                                currentPathwayIndex = currentPathwayIndex,
                                subtitle = currentPathway.name,
                                languages =  currentPathway.supportedLanguages,
                                selectedLanguage = selectedLanguage,
                                logo = currentPathway.logo,
                                showTakeTestButton = if(currentPathway.cta?.url?.isBlank()?:true) false else true
                            )
                        }
                    } else {
                        setState { copy(loading = false) }
                    }
                }
            }
//            getBatchesDataByPathway(corePreferences.lastSelectedPathWayId)

        }

    }

    private fun refreshCourses(pathway: Pathway, forceUpdate: Boolean) {
        viewModelScope.launch(Dispatchers.Default) {
            learnRepo.getCoursesDataByPathway(pathway.id, forceUpdate).collect {
                it?.let {
                    setState { copy(courses = it, loading = false, logo = pathway.logo,
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
                checkedStudentEnrolment(1)
//                getBatchesDataByPathway(1)
            }
//            is LearnFragmentViewActions.ShowUpcomingClasses ->{
//                getUpcomingClasses(1)
//            }

            is LearnFragmentViewActions.PrimaryAction -> primaryAction(actions.classId)
            LearnFragmentViewActions.RefreshCourses -> {
                val currentState = viewState.value!!
                if (currentState.pathways.size > currentState.currentPathwayIndex) {
                    refreshCourses(pathway = currentState.pathways[currentState.currentPathwayIndex], true)
                } else {
                    //TO hide progress bar
                    setState { copy() }
                }
            }
            LearnFragmentViewActions.LanguageSelectionClicked -> {
                _viewEvents.postValue(LearnFragmentViewEvents.OpenLanguageSelectionSheet)
            }
            LearnFragmentViewActions.BtnMoreBatchClicked ->{
                val currentState = viewState.value!!
                _viewEvents.postValue(LearnFragmentViewEvents.OpenBatchSelectionSheet(currentState.batches))
            }
            is LearnFragmentViewActions.ShowUpcomingClasses ->{
                val currentState = viewState.value!!
                _viewEvents.postValue(LearnFragmentViewEvents.ShowUpcomingClasses(currentState.classes))
            }

            LearnFragmentViewActions.PathwayCtaClicked -> {
                val currentState = viewState.value!!
                _viewEvents.postValue(LearnFragmentViewEvents.OpenUrl(currentState.pathways[currentState.currentPathwayIndex].cta))
            }
//            LearnFragmentViewActions.BatchSelectClicked ->{
//                _viewEvents.postValue(LearnFragmentViewEvents.BatchSelectClicked(it.batch))
//            }

        }
    }



     private fun checkedStudentEnrolment(pathwayId: Int){
        viewModelScope.launch {
            setState { copy(loading=true) }
            val status = learnRepo.checkedStudentEnrolment(pathwayId).message
            if(status == EnrolStatus.enrolled){
                getUpcomingClasses(1)
            } else if(status == EnrolStatus.not_enrolled){
                getBatchesDataByPathway(pathwayId)
            }

        }

    }

    private fun getBatchesDataByPathway(pathwayId: Int) {
        viewModelScope.launch {
            setState { copy(loading=true) }
            val batches =learnRepo.getBatchesListByPathway(pathwayId)
            batches.let {
                setState {
                    copy(
                        batches = it
                    )
                }
                if(it.isNotEmpty()){
                    _viewEvents.postValue(LearnFragmentViewEvents.ShowUpcomingBatch(it[0]))
                }
            }
        }
    }


    private fun getUpcomingClasses(pathwayId: Int){
        viewModelScope.launch {
            setState { copy(loading= true) }
            val classes = learnRepo.getUpcomingClass(pathwayId)
            classes?.let {
                setState {
                    copy(
                        classes = it
                    )
                }
                if (it.isNotEmpty()){
//                    _viewEvents.postValue(LearnFragmentViewEvents.ShowUpcomingClasses(classes))
//                    liveDataList.postValue(classes)
                }
            }
        }
    }

    fun selectBatch(batch: Batch) {
        _viewEvents.postValue(LearnFragmentViewEvents.BatchSelectClicked(batch))
    }

    private fun primaryAction(classId: Int) {
        viewModelScope.launch {
        setState { copy(loading = true) }
        val result = learnRepo.enrollToClass(classId, false)
        if (result) {
            isEnrolled = true
//            val durationToClassStart = (classes.startTime.time - Date().time)
            setState {
                copy(
                    loading = false,
                )
            }
            _viewEvents.setValue(LearnFragmentViewEvents.ShowToast(stringProvider.getString(R.string.enrolled)))
        } else {
            setState { copy(loading = false) }
            _viewEvents.setValue(LearnFragmentViewEvents.ShowToast(stringProvider.getString(R.string.unable_to_enroll)))
        }
        } }




    }


data class LearnFragmentViewState(
    val loading: Boolean = false,
    val subtitle: String? = null,
    val pathways: List<Pathway> = arrayListOf(),
    val batches: List<Batch> = arrayListOf(),
    val classes: List<UpcomingClass> = arrayListOf(),
    val currentPathwayIndex: Int = 0,
    val courses: List<Course> = arrayListOf(),
    val selectedLanguage: String? = null,
    val languages: List<Language> = arrayListOf(),
    val logo: String? = null,
    val showTakeTestButton: Boolean = false,
    val menuId: Int? = null,
    val classId: Int = 0
) : ViewState

sealed class LearnFragmentViewEvents : ViewEvents {
    class ShowToast(val toastText: String) :LearnFragmentViewEvents()
    data class OpenBatchSelectionSheet(val batches: List<Batch>):LearnFragmentViewEvents()
    data class ShowUpcomingBatch(val batch: Batch):LearnFragmentViewEvents()
    data class ShowUpcomingClasses(val classes: List<UpcomingClass>) : LearnFragmentViewEvents()
    object OpenPathwaySelectionSheet : LearnFragmentViewEvents()
    object OpenLanguageSelectionSheet : LearnFragmentViewEvents()
    data class BatchSelectClicked(val batch: Batch) : LearnFragmentViewEvents()
    object DismissSelectionSheet : LearnFragmentViewEvents()
    class OpenCourseDetailActivity(val courseId: String, val courseName: String, val pathwayId: Int) :
        LearnFragmentViewEvents()
    data class OpenUrl(val cta: PathwayCTA?) : LearnFragmentViewEvents()

}

sealed class LearnFragmentViewActions : ViewModelAction {
    object RequestPageLoad :LearnFragmentViewActions()
    object ShowUpcomingClasses : LearnFragmentViewActions()
    data class PrimaryAction(val classId: Int) : LearnFragmentViewActions()
    object ToolbarClicked : LearnFragmentViewActions()
    object BtnMoreBatchClicked: LearnFragmentViewActions()
    object LanguageSelectionClicked : LearnFragmentViewActions()
    object RefreshCourses : LearnFragmentViewActions()
    object PathwayCtaClicked : LearnFragmentViewActions()
}
