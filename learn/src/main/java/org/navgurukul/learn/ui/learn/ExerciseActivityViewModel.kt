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
import org.navgurukul.learn.courses.db.models.Course
import org.navgurukul.learn.courses.db.models.CurrentStudy
import org.navgurukul.learn.courses.db.models.Exercise
import org.navgurukul.learn.courses.db.models.ExerciseProgress
import org.navgurukul.learn.courses.repository.LearnRepo
import org.merakilearn.core.utils.CorePreferences

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
            learnRepo.fetchCourseExerciseDataWithCourse(courseId, pathwayId, selectedLanguage)
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
                        launchLastSelectedExerciseOfCourse(course)
                    }
                }

        }

    }

    private suspend fun launchLastSelectedExerciseOfCourse(course: Course?) {
        course?.let {
            currentCourse = it

            setState {
                copy(
                    isLoading = false,
                    currentCourseTitle = currentCourse.name,
                    exerciseList = currentCourse.exercises,
                    isCourseCompleted = false
                )
            }

            val exerciseId = withContext(Dispatchers.IO) {
                learnRepo.fetchCurrentStudyForCourse(course.id)
            }?.exerciseId ?: currentCourse.exercises.first().id
            onExerciseListItemSelected(exerciseId)
        }
    }

    fun handle(action: ExerciseActivityViewActions) {
        when (action) {
            is ExerciseActivityViewActions.MarkCompleteClicked -> markCourseExerciseCompleted(currentStudy?.exerciseId)
            is ExerciseActivityViewActions.ExerciseListItemSelected -> onExerciseListItemSelected(
                action.exerciseId
            )
            is ExerciseActivityViewActions.ExerciseMarkedCompleted -> onExerciseMarkedCompleted()
            is ExerciseActivityViewActions.NextNavigationClicked -> navigate(ExerciseNavigation.NEXT)
            is ExerciseActivityViewActions.PrevNavigationClicked -> navigate(ExerciseNavigation.PREV)
            is ExerciseActivityViewActions.OnNextCourseClicked -> launchNextCourse(currentCourse.id)
        }
    }

    private fun navigate(navigation: ExerciseNavigation) {
        val currentStudyIndex = currentCourse.exercises.indexOfFirst {
            it.id == currentStudy?.exerciseId
        }
        if (navigation == ExerciseNavigation.PREV && currentStudyIndex > 0) {
            onExerciseListItemSelected(
                currentCourse.exercises[currentStudyIndex - 1].id,
                navigation
            )
        } else if (navigation == ExerciseNavigation.NEXT && currentStudyIndex < currentCourse.exercises.size - 1) {
            onExerciseListItemSelected(
                currentCourse.exercises[currentStudyIndex + 1].id,
                navigation
            )
        } else if (navigation == ExerciseNavigation.NEXT && currentStudyIndex == currentCourse.exercises.size - 1) {
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
    private fun saveCourseExerciseCurrent(
        currentStudy: CurrentStudy
    ) {
        viewModelScope.launch {
            learnRepo.saveCourseExerciseCurrent(currentStudy)
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
        markExerciseSelected(exerciseId)

        val index = currentCourse.exercises.indexOfFirst { exerciseId == it.id }
        val isFirst = index == 0
        val isLast = index == currentCourse.exercises.size - 1
        val isCompleted =
            currentCourse.exercises[index].exerciseProgress == ExerciseProgress.COMPLETED
        _viewEvents.setValue(
            ExerciseActivityViewEvents.ShowExerciseFragment(
                isFirst, isLast, isCompleted, currentCourse.id, exerciseId, navigation
            )
        )
    }
    private fun markCourseExerciseCompleted(
        exerciseId: String?
    ) {
        exerciseId?.let {
            viewModelScope.launch {
                learnRepo.markCourseExerciseCompleted(it)
            }
        }
    }

    private fun onExerciseMarkedCompleted() {
        val updatedList = currentCourse
            .exercises.toMutableList()

        val completedExercise = updatedList.find {
            currentStudy?.exerciseId == it.id
        }
        completedExercise?.let {
            it.exerciseProgress = ExerciseProgress.COMPLETED

            setState { copy(exerciseList = updatedList) }
        }
    }

    private fun launchNextCourse(currentCourseId: String) {
        //course without exercise details
        getNextCourse(currentCourseId)?.let {
            viewModelScope.launch {
                learnRepo.fetchCourseExerciseDataWithCourse(it.id, pathwayId, selectedLanguage)
                    .collect {
                        //course with exercise details
                        launchLastSelectedExerciseOfCourse(it)
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

    private fun markExerciseSelected(exerciseId: String) {
        var selectedIndex = 0
        currentCourse.exercises.toMutableList().let {
            it.forEachIndexed { index, exercise ->
                if (exercise.id == exerciseId) {
                    if (exercise.exerciseProgress != ExerciseProgress.COMPLETED) {
                        exercise.exerciseProgress = ExerciseProgress.IN_PROGRESS
                        selectedIndex = index
                    }
                } else {
                    if (exercise.exerciseProgress == ExerciseProgress.IN_PROGRESS) {
                        exercise.exerciseProgress = ExerciseProgress.NOT_STARTED
                    }
                }
            }

            setState { copy(exerciseList = it, currentExerciseIndex = selectedIndex) }
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
        val exerciseId: String,
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
    val currentExerciseIndex: Int = 0,
    val exerciseList: List<Exercise> = listOf(),
) : ViewState

sealed class ExerciseActivityViewActions : ViewModelAction {
    object MarkCompleteClicked : ExerciseActivityViewActions()
    object OnNextCourseClicked : ExerciseActivityViewActions()
    object PrevNavigationClicked : ExerciseActivityViewActions()
    object NextNavigationClicked : ExerciseActivityViewActions()
    data class ExerciseListItemSelected(val exerciseId: String) : ExerciseActivityViewActions()
    object ExerciseMarkedCompleted : ExerciseActivityViewActions()
}
