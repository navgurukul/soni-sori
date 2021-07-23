package org.navgurukul.learn.ui.learn

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.merakilearn.core.datasource.Config
import org.merakilearn.core.datasource.model.Language
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.ViewEvents
import org.navgurukul.commonui.platform.ViewModelAction
import org.navgurukul.commonui.platform.ViewState
import org.navgurukul.learn.courses.db.models.Course
import org.navgurukul.learn.courses.db.models.CurrentStudy
import org.navgurukul.learn.courses.db.models.Pathway
import org.navgurukul.learn.courses.repository.LearnRepo
import org.navgurukul.learn.util.LearnPreferences

class LearnFragmentViewModel(
    private val learnRepo: LearnRepo,
    private val learnPreferences: LearnPreferences,
    config: Config
) :
    BaseViewModel<LearnFragmentViewEvents, LearnFragmentViewState>(LearnFragmentViewState()) {

    private val supportedLanguages: List<Language> = config.getObjectifiedList(Config.COURSE_AVAILABLE_LANG)!!

    init {
        setState { copy(loading = true) }
        viewModelScope.launch(Dispatchers.Default) {
            learnRepo.getPathwayData(true).collect {
                it?.let {
                    if (it.isNotEmpty()) {
                        setState {
                            val lastSelectedPathwayId = learnPreferences.lastSelectedPathWayId
                            val selectedLanguage = learnPreferences.selectedLanguage
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
                            copy(
                                loading = courses.isEmpty(),
                                pathways = it,
                                courses = courses,
                                currentPathwayIndex = currentPathwayIndex,
                                subtitle = currentPathway.name,
                                languages = supportedLanguages,
                                selectedLanguage = supportedLanguages.find { it.code == selectedLanguage }!!.label
                            )
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
            learnRepo.getCoursesDataByPathway(pathway.id, forceUpdate).collect {
                it?.let { setState { copy(courses = it, loading = false) } }
            }
        }
    }

    fun selectPathway(pathway: Pathway) {
        setState {
            copy(
                currentPathwayIndex = pathways.indexOf(pathway),
                subtitle = pathway.name,
                loading = true
            )
        }
        learnPreferences.lastSelectedPathWayId = pathway.id
        _viewEvents.postValue(LearnFragmentViewEvents.DismissSelectionSheet)
        refreshCourses(pathway, false)
    }

    fun selectLanguage(language: Language) {
        setState {
            copy(
                selectedLanguage = language.label
            )
        }
        learnPreferences.selectedLanguage = language.code!!
        _viewEvents.postValue(LearnFragmentViewEvents.DismissSelectionSheet)
    }

    fun selectCourse(course: Course) {
        viewModelScope.launch(Dispatchers.Default) {
            learnRepo.fetchCurrentStudyForCourse(course.id).let {
                if (it.isNotEmpty()) {
                    _viewEvents.postValue(LearnFragmentViewEvents.OpenCourseSlugActivity(it.first()))
                } else {
                    _viewEvents.postValue(
                        LearnFragmentViewEvents.OpenCourseDetailActivity(course.id, course.name)
                    )
                }
            }
        }
    }

    fun handle(actions: LearnFragmentViewActions) {
        when (actions) {
            is LearnFragmentViewActions.ToolbarClicked -> {
                _viewEvents.postValue(LearnFragmentViewEvents.OpenPathwaySelectionSheet)
            }
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
        }
    }
}

data class LearnFragmentViewState(
    val loading: Boolean = false,
    val subtitle: String? = null,
    val pathways: List<Pathway> = arrayListOf(),
    val currentPathwayIndex: Int = 0,
    val courses: List<Course> = arrayListOf(),
    val selectedLanguage: String? = null,
    val languages: List<Language> = arrayListOf()
) : ViewState

sealed class LearnFragmentViewEvents : ViewEvents {
    object OpenPathwaySelectionSheet : LearnFragmentViewEvents()
    object OpenLanguageSelectionSheet : LearnFragmentViewEvents()
    object DismissSelectionSheet : LearnFragmentViewEvents()
    class OpenCourseSlugActivity(val currentStudy: CurrentStudy) : LearnFragmentViewEvents()
    class OpenCourseDetailActivity(val courseId: String, val courseName: String) :
        LearnFragmentViewEvents()
}

sealed class LearnFragmentViewActions : ViewModelAction {
    object ToolbarClicked : LearnFragmentViewActions()
    object LanguageSelectionClicked : LearnFragmentViewActions()
    object RefreshCourses : LearnFragmentViewActions()
}
