package org.navgurukul.learn.ui.learn

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.ViewEvents
import org.navgurukul.commonui.platform.ViewModelAction
import org.navgurukul.commonui.platform.ViewState
import org.navgurukul.learn.courses.db.models.Course
import org.navgurukul.learn.courses.db.models.CurrentStudy
import org.navgurukul.learn.courses.db.models.Pathway
import org.navgurukul.learn.courses.repository.LearnRepo

class LearnFragmentViewModel(private val learnRepo: LearnRepo) :
    BaseViewModel<LearnFragmentViewEvents, LearnFragmentViewState>(LearnFragmentViewState()) {

    init {
        setState { copy(loading = true) }
        viewModelScope.launch(Dispatchers.Default) {
            learnRepo.getPathwayData(false).collect {
                it?.let {
                    if (it.isNotEmpty()) {
                        setState {
                            val currentPathway =  it[currentPathwayIndex]
                            val courses = currentPathway.courses
                            if (currentPathway.courses.isEmpty()) {
                                selectPathway(currentPathway)
                            }
                            copy(
                                loading = courses.isEmpty(),
                                pathways = it,
                                courses = courses,
                                subtitle = currentPathway.name
                            )
                        }
                    } else {
                        setState { copy(loading = false) }
                    }
                }
            }
        }
    }

    fun refreshCourses() {
        val currentState = viewState.value!!
        if (currentState.pathways.size > currentState.currentPathwayIndex) {
            selectPathway(currentState.pathways[currentState.currentPathwayIndex])
        } else {
            setState { copy() }
        }
    }

    fun selectPathway(pathway: Pathway, forceUpdate: Boolean = false) {
        setState { copy(currentPathwayIndex = pathways.indexOf(pathway), subtitle = pathway.name, loading = true) }
        _viewEvents.postValue(LearnFragmentViewEvents.DismissPathwaySelectionSheet)
        viewModelScope.launch(Dispatchers.Default) {
            learnRepo.getCoursesDataByPathway(pathway.id, forceUpdate).collect {
                it?.let { setState { copy(courses = it, loading = false) } }
            }
        }
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
        when(actions) {
            is LearnFragmentViewActions.ToolbarClicked -> {
                _viewEvents.postValue(LearnFragmentViewEvents.OpenPathwaySelectionSheet)
            }
        }
    }
}

data class LearnFragmentViewState(
    val loading: Boolean = false,
    val subtitle: String? = null,
    val pathways: List<Pathway> = arrayListOf(),
    val currentPathwayIndex: Int = 0,
    val courses: List<Course> = arrayListOf()
) : ViewState

sealed class LearnFragmentViewEvents : ViewEvents {
    object OpenPathwaySelectionSheet : LearnFragmentViewEvents()
    object DismissPathwaySelectionSheet : LearnFragmentViewEvents()
    class OpenCourseSlugActivity(val currentStudy: CurrentStudy) : LearnFragmentViewEvents()
    class OpenCourseDetailActivity(val courseId: String, val courseName: String) :
        LearnFragmentViewEvents()
}

sealed class LearnFragmentViewActions: ViewModelAction {
    object ToolbarClicked: LearnFragmentViewActions()
}
