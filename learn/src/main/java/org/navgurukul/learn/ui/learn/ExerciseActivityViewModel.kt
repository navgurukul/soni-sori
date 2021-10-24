package org.navgurukul.learn.ui.learn

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
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
import org.navgurukul.learn.util.LearnPreferences

class ExerciseActivityViewModel(
    private val learnRepo: LearnRepo,
    learnPreferences: LearnPreferences,
    private val stringProvider: StringProvider,
    private var courseId: String,
    private val pathwayId: Int,
) : BaseViewModel<ExerciseActivityViewEvents, ExerciseActivityViewState>(
    ExerciseActivityViewState()
) {

    private lateinit var currentCourse: Course
    private var coursesList: List<Course>? = null
    private val selectedLanguage = learnPreferences.selectedLanguage
    private var currentStudy: CurrentStudy? = null

    init {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            learnRepo.fetchCourseExerciseDataWithCourse(courseId, selectedLanguage)
                .collect { course ->
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

        getCourseList()
    }

    private fun getCourseList() {
        viewModelScope.launch {
            learnRepo.getCoursesDataByPathway(pathwayId, false).collect {
                coursesList = it
            }
        }
    }

    suspend fun launchLastSelectedExerciseOfCourse(course: Course?) {
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
        val nextExercise = if (navigation == ExerciseNavigation.PREV && currentStudyIndex > 0) {
            currentCourse.exercises[currentStudyIndex - 1]
        } else if (navigation == ExerciseNavigation.NEXT && currentStudyIndex < currentCourse.exercises.size - 1) {
            currentCourse.exercises[currentStudyIndex + 1]
        } else if (navigation == ExerciseNavigation.NEXT && currentStudyIndex == currentCourse.exercises.size - 1) {
            setState {
                copy(
                    isCourseCompleted = true,
                    nextCourseTitle = getNextCourse(currentCourse.id)?.name ?: ""
                )
            }
            null
        } else {
            null
        }

        nextExercise?.let { onExerciseListItemSelected(nextExercise.id, navigation) }
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
        var nextCourse = getNextCourse(currentCourseId)
        nextCourse?.let {
//            setState { copy(isCourseCompleted = false) }
            viewModelScope.launch {
                learnRepo.fetchCourseExerciseDataWithCourse(it.id, selectedLanguage).collect {
                    //course with exercise details
                    nextCourse = it
                    launchLastSelectedExerciseOfCourse(nextCourse)
                }
            }
        }

    }

    private fun getNextCourse(currentCourseId: String): Course? {
        val currentCourseIndex = coursesList?.indexOfFirst { it.id == currentCourseId }

        currentCourseIndex?.let { index ->
            if (index > 0 && index < coursesList?.size?.minus(1) ?: 0) {
                coursesList?.let {
                    return it[currentCourseIndex + 1]
                }
            }
        }
        return null
    }

    private fun markExerciseSelected(exerciseId: String) {
        currentCourse.exercises.toMutableList().let {
            it.forEach { exercise ->
                if (exercise.id == exerciseId) {
                    if (exercise.exerciseProgress != ExerciseProgress.COMPLETED) {
                        exercise.exerciseProgress = ExerciseProgress.IN_PROGRESS
                    }
                } else {
                    if (exercise.exerciseProgress == ExerciseProgress.IN_PROGRESS) {
                        exercise.exerciseProgress = ExerciseProgress.NOT_STARTED
                    }
                }
            }

            setState { copy(exerciseList = it) }
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

    class ShowToast(val toastText: String) : ExerciseActivityViewEvents()
}

data class ExerciseActivityViewState(
    val isLoading: Boolean = false,
    val isCourseCompleted: Boolean = false,
    val currentCourseTitle: String = "",
    val nextCourseTitle: String = "",
    val exerciseList: List<Exercise> = listOf(),
) : ViewState

sealed class ExerciseActivityViewActions : ViewModelAction {
    object OnNextCourseClicked : ExerciseActivityViewActions()
    object PrevNavigationClicked : ExerciseActivityViewActions()
    object NextNavigationClicked : ExerciseActivityViewActions()
    data class ExerciseListItemSelected(val exerciseId: String) : ExerciseActivityViewActions()
    object ExerciseMarkedCompleted : ExerciseActivityViewActions()
}
