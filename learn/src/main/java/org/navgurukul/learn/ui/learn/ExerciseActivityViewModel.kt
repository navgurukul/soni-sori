package org.navgurukul.learn.ui.learn

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.ViewEvents
import org.navgurukul.commonui.platform.ViewModelAction
import org.navgurukul.commonui.platform.ViewState
import org.navgurukul.commonui.resources.StringProvider
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.repository.LearnRepo
import org.merakilearn.core.utils.CorePreferences
import org.navgurukul.learn.courses.db.models.*

class ExerciseActivityViewModel(
    private val learnRepo: LearnRepo,
    corePreferences: CorePreferences,
    private val stringProvider: StringProvider,
    private var courseId: String,
    private val pathwayId: Int,
) : BaseViewModel<ExerciseActivityViewEvents, ExerciseActivityViewState>(
    ExerciseActivityViewState()
) {

    private lateinit var currentCourse: Course
    private var coursesList: List<Course>? = null
    private val selectedLanguage = corePreferences.selectedLanguage
    private var currentStudy: CurrentStudy? = null

    init {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            learnRepo.fetchCourseContentDataWithCourse(courseId, pathwayId, selectedLanguage)
                .combine(
                    learnRepo.getCoursesDataByPathway(
                        pathwayId,
                        false
                    )
                ) { course, courseList ->
                    course to courseList
                }
                .collect {
                    val course = it.first
                    coursesList = it.second
                    if (course == null) {
                        setState { copy(isLoading = false) }
                        _viewEvents.postValue(
                            ExerciseActivityViewEvents.ShowToast(stringProvider.getString(R.string.error_loading_data))
                        )
                    } else {
                        launchLastSelectedContentOfCourse(course)
                    }
                }

        }

    }

    private suspend fun launchLastSelectedContentOfCourse(course: Course?) {
        course?.let {
            currentCourse = it

            setState {
                copy(
                    isLoading = false,
                    currentCourseTitle = currentCourse.name,
                    courseContentList = currentCourse.courseContents,
                    isCourseCompleted = false
                )
            }

            val exerciseId = withContext(Dispatchers.IO) {
                learnRepo.fetchCurrentStudyForCourse(course.id)
            }?.exerciseId ?: currentCourse.courseContents.first().id
            onExerciseListItemSelected(exerciseId)
        }
    }

    fun handle(action: ExerciseActivityViewActions) {
        when (action) {
            is ExerciseActivityViewActions.ContentListItemSelected -> onExerciseListItemSelected(
                action.contentId
            )
            is ExerciseActivityViewActions.ContentMarkedCompleted -> onExerciseMarkedCompleted()
            is ExerciseActivityViewActions.NextNavigationClicked -> navigate(ExerciseNavigation.NEXT)
            is ExerciseActivityViewActions.PrevNavigationClicked -> navigate(ExerciseNavigation.PREV)
            is ExerciseActivityViewActions.OnNextCourseClicked -> launchNextCourse(currentCourse.id)
        }
    }

    private fun navigate(navigation: ExerciseNavigation) {
        val currentStudyIndex = currentCourse.courseContents.indexOfFirst {
            it.id == currentStudy?.exerciseId
        }
        if (navigation == ExerciseNavigation.PREV && currentStudyIndex > 0) {
            onExerciseListItemSelected(
                currentCourse.courseContents[currentStudyIndex - 1].id,
                navigation
            )
        } else if (navigation == ExerciseNavigation.NEXT && currentStudyIndex < currentCourse.courseContents.size - 1) {
            onExerciseListItemSelected(
                currentCourse.courseContents[currentStudyIndex + 1].id,
                navigation
            )
        } else if (navigation == ExerciseNavigation.NEXT && currentStudyIndex == currentCourse.courseContents.size - 1) {
            val nextActionTitle: String = getNextCourse(currentCourse.id)?.let {
                stringProvider.getString(
                    R.string.next_course_message,
                    it.name
                )
            } ?: stringProvider.getString(R.string.finish)
            setState {
                copy(
                    isCourseCompleted = true,
                    nextCourseTitle = nextActionTitle
                )
            }
        }
    }

    private fun onExerciseListItemSelected(
        exerciseId: String,
        navigation: ExerciseNavigation? = null
    ) {
        currentStudy = CurrentStudy(currentCourse.id, exerciseId).apply {
            viewModelScope.launch(Dispatchers.IO) {
                learnRepo.saveCourseExerciseCurrent(this@apply)
            }
        }
        markContentSelected(exerciseId)

        val index = currentCourse.courseContents.indexOfFirst { exerciseId == it.id }
        val isFirst = index == 0
        val isLast = index == currentCourse.courseContents.size - 1
        val isCompleted =
            currentCourse.courseContents[index].courseContentProgress == CourseContentProgress.COMPLETED
        val courseContentType = currentCourse.courseContents[index].contentContentType

        _viewEvents.setValue(
            ExerciseActivityViewEvents.ShowExerciseFragment(
                isFirst, isLast, isCompleted, currentCourse.id, exerciseId, courseContentType, navigation
            )
        )
    }

    private fun onExerciseMarkedCompleted() {
        val updatedList = currentCourse
            .courseContents.toMutableList()

        val completedExercise = updatedList.find {
            currentStudy?.exerciseId == it.id
        }
        completedExercise?.let {
            it.courseContentProgress = CourseContentProgress.COMPLETED

            setState { copy(courseContentList = updatedList) }
        }
    }

    private fun launchNextCourse(currentCourseId: String) {
        //course without exercise details
        getNextCourse(currentCourseId)?.let {
            viewModelScope.launch {
                learnRepo.fetchCourseContentDataWithCourse(it.id, pathwayId, selectedLanguage)
                    .collect {
                        //course with exercise details
                        launchLastSelectedContentOfCourse(it)
                    }
            }
        } ?: run {
            _viewEvents.postValue(
                ExerciseActivityViewEvents.FinishActivity
            )
        }

    }

    private fun getNextCourse(currentCourseId: String): Course? {
        val coursesList = coursesList ?: return null
        val currentCourseIndex = coursesList.indexOfFirst { it.id == currentCourseId }

        currentCourseIndex.let { index ->
            if (index >= 0 && index < coursesList.size.minus(1)) {
                return coursesList[currentCourseIndex + 1]
            }
        }
        return null
    }

    private fun markContentSelected(contentId: String) {
        var selectedIndex = 0
        currentCourse.courseContents.toMutableList().let {
            it.forEachIndexed { index, content ->
                if (content.id == contentId) {
                    if (content.courseContentProgress != CourseContentProgress.COMPLETED) {
                        content.courseContentProgress = CourseContentProgress.IN_PROGRESS
                        selectedIndex = index
                    }
                } else {
                    if (content.courseContentProgress == CourseContentProgress.IN_PROGRESS) {
                        content.courseContentProgress = CourseContentProgress.NOT_STARTED
                    }
                }
            }

            setState { copy(courseContentList = it, currentContentIndex = selectedIndex) }
        }
    }
}

enum class ExerciseNavigation { PREV, NEXT }

sealed class ExerciseActivityViewEvents : ViewEvents {
    class ShowExerciseFragment(
        val isFirst: Boolean,
        val isLast: Boolean,
        val isCompleted: Boolean,
        val courseId: String,
        val contentId: String,
        val courseContentType: CourseContentType,
        val navigation: ExerciseNavigation?
    ) : ExerciseActivityViewEvents()

    class ShowClassFragment(
        val isFirst: Boolean,
        val isLast: Boolean,
        val isCompleted: Boolean,
        val courseId: String,
        val contentId: String,
        val courseContentType: CourseContentType,
        val navigation: ExerciseNavigation?
    ) : ExerciseActivityViewEvents()

    object FinishActivity : ExerciseActivityViewEvents()
    class ShowToast(val toastText: String) : ExerciseActivityViewEvents()
}

data class ExerciseActivityViewState(
    val isLoading: Boolean = false,
    val isCourseCompleted: Boolean = false,
    val currentCourseTitle: String = "",
    val nextCourseTitle: String = "",
    val currentContentIndex: Int = 0,
    val courseContentList: List<CourseContents> = listOf(),
) : ViewState

sealed class ExerciseActivityViewActions : ViewModelAction {
    object OnNextCourseClicked : ExerciseActivityViewActions()
    object PrevNavigationClicked : ExerciseActivityViewActions()
    object NextNavigationClicked : ExerciseActivityViewActions()
    data class ContentListItemSelected(val contentId: String) : ExerciseActivityViewActions()
    object ContentMarkedCompleted : ExerciseActivityViewActions()
}
