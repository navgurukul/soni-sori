package org.navgurukul.learn.ui.learn

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
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
    private val learnPreferences: LearnPreferences,
    private val stringProvider: StringProvider,
    private val courseId: String,
    private val currentStudy: CurrentStudy? = null
) : BaseViewModel<ExerciseActivityViewModel.ExerciseActivityViewEvents, ExerciseActivityViewModel.ExerciseActivityViewState>(
    ExerciseActivityViewState()
) {

    private var currentCourseExerciseList: List<Exercise>? = null
    private var fetchCourseExerciseJob: Job? = null
    private var fetchCourseExerciseWithCourseJob: Job? = null
    private val selectedLanguage = learnPreferences.selectedLanguage

    init {
        fetchCourseExerciseData(courseId, currentStudy)
        fetchCourseExerciseDataWithCourse(courseId, currentStudy)
    }

    fun handle(action: ExerciseActivityViewActions) {
        when (action) {
            is ExerciseActivityViewActions.FetchCourseExercises -> fetchCourseExerciseData(
                action.courseId,
                currentStudy,
            )
            is ExerciseActivityViewActions.FetchCourseExerciseDataWithCourse -> fetchCourseExerciseDataWithCourse(
                action.courseId,
                currentStudy,
            )
            is ExerciseActivityViewActions.ExerciseListItemSelected -> {
                onExerciseListItemSelected(
                    action.selectedStudy,
                )
                markExerciseSelected(action.selectedStudy)
//
                action.currentStudy?.let { markExerciseUnselected(it) }
            }
            is ExerciseActivityViewActions.ExerciseMarkedCompleted -> onExerciseMarkedCompleted(
                action.currentStudy,
            )
            is ExerciseActivityViewActions.NextNavigationClicked -> onNextListItemRequested(
                action.currentStudy
            )
            is ExerciseActivityViewActions.PrevNavigationClicked -> onPrevListItemRequested(
                action.currentStudy
            )
            is ExerciseActivityViewActions.FirstTimeLaunch -> showFirstExerciseOfCourse(
                action.courseId, action.courseName
            )
        }
    }

    private fun showFirstExerciseOfCourse(courseId: String, courseName: String? = null) {
        if (!currentCourseExerciseList.isNullOrEmpty()) {
            val firstItem = currentCourseExerciseList!![0]
            onExerciseListItemSelected(
                CurrentStudy(
                    courseId, firstItem.courseName,
                    firstItem.slug, firstItem.name, firstItem.id
                )
            )
        }
    }

    private fun markExerciseUnselected(currenStudy: CurrentStudy) {
        currentCourseExerciseList?.let { list ->
            val updatedList = list.toMutableList()

            val currentExercise = updatedList.find {
                currenStudy.exerciseId == it.id
            }
            currentExercise?.let {
                if (it.exerciseProgress == ExerciseProgress.IN_PROGRESS) {
                    it.exerciseProgress = ExerciseProgress.NOT_STARTED
                }
                onExerciseListUpdated(updatedList)
            }
        }

    }

    private fun onExerciseListUpdated(updatedList: MutableList<Exercise>) {
        currentCourseExerciseList = updatedList
        _viewEvents.setValue(ExerciseActivityViewEvents.UpdateList(updatedList))
    }

    private fun onNextListItemRequested(currentStudy: CurrentStudy) {
        currentCourseExerciseList?.let { list ->
            val currentStudyIndex = list.indexOfFirst {
                it.id == currentStudy.exerciseId
            }
            if (currentStudyIndex < list.size - 1) {
                val nextExercise = list[currentStudyIndex + 1]
                val nextStudy = CurrentStudy(
                    courseId,
                    nextExercise.courseName,
                    nextExercise.slug,
                    nextExercise.name,
                    nextExercise.id
                )
                onExerciseListItemSelected(nextStudy)
                markExerciseSelected(nextStudy)
                markExerciseUnselected(currentStudy)
            }
        }
    }

    private fun onPrevListItemRequested(currentStudy: CurrentStudy) {
        currentCourseExerciseList?.let { list ->
            val currentStudyIndex = list.indexOfFirst {
                it.id == currentStudy.exerciseId
            }
            if (currentStudyIndex > 0) {
                val nextExercise = list[currentStudyIndex - 1]
                val nextStudy = CurrentStudy(
                    courseId,
                    nextExercise.courseName,
                    nextExercise.slug,
                    nextExercise.name,
                    nextExercise.id
                )
                onExerciseListItemSelected(nextStudy)
                markExerciseSelected(nextStudy)
                markExerciseUnselected(currentStudy)
            }
        }
    }

    private fun onExerciseListItemSelected(selectedStudy: CurrentStudy) {
        CoroutineScope(Dispatchers.Main).launch {
            currentCourseExerciseList?.let { list ->
                val index = list.indexOfFirst { selectedStudy.exerciseId == it.id }
                val isFirst = if (index == 0) true else false
                val isLast = if (index == list.size - 1) true else false
                val isCompleted =
                    if (list[index].exerciseProgress == ExerciseProgress.COMPLETED) true else false
                _viewEvents.setValue(
                    ExerciseActivityViewEvents.ShowExerciseFragment(
                        selectedStudy,
                        isFirst,
                        isLast,
                        isCompleted
                    )
                )
            }
        }
    }

    private fun onExerciseMarkedCompleted(completedStudy: CurrentStudy) {
        currentCourseExerciseList?.let { list ->
            val updatedList = list.toMutableList()

            val completedExercise = updatedList.find {
                completedStudy.exerciseId == it.id
            }
            completedExercise?.let {
                it.exerciseProgress = ExerciseProgress.COMPLETED

                onExerciseListUpdated(updatedList)
            }
        }
    }

    private fun markExerciseSelected(selectedStudy: CurrentStudy) {
        currentCourseExerciseList?.let { list ->
            val updatedList = list.toMutableList()

            val currentExercise = updatedList.find {
                selectedStudy.exerciseId == it.id
            }
            currentExercise?.let {
                if (it.exerciseProgress != ExerciseProgress.COMPLETED) {
                    it.exerciseProgress = ExerciseProgress.IN_PROGRESS
                }
                onExerciseListUpdated(updatedList)
            }
        }
    }

    fun fetchCourseExerciseData(courseId: String, currentStudy: CurrentStudy?) {
        fetchCourseExerciseJob?.cancel()
        fetchCourseExerciseJob = viewModelScope.launch {
            setState { copy(isLoading = true) }
            val response =
                learnRepo.getCoursesExerciseData(courseId, selectedLanguage)
                    .collect {
                        currentCourseExerciseList = it
                        val list = it

                        setState { copy(isLoading = false) }

                        if (list != null) {
                            _viewEvents.postValue(ExerciseActivityViewEvents.ShowExerciseList(list))
                        } else {
                            _viewEvents.postValue(
                                ExerciseActivityViewEvents.ShowToast(
                                    stringProvider.getString(
                                        R.string.error_loading_data
                                    )
                                )
                            )
                        }
                    }
        }
    }

    fun fetchCourseExerciseDataWithCourse(courseId: String, currentStudy: CurrentStudy?) {
        fetchCourseExerciseWithCourseJob?.cancel()
        fetchCourseExerciseWithCourseJob = viewModelScope.launch {
            setState { copy(isLoading = true) }
            val response =
                learnRepo.fetchCourseExerciseDataWithCourse(courseId, selectedLanguage)
                    .collect {
                        val list = it

                        setState { copy(isLoading = false) }

                        if (list != null) {
                            _viewEvents.postValue(
                                ExerciseActivityViewEvents.SetCourseAndRenderUI(
                                    list
                                )
                            )
                        } else {
                            _viewEvents.postValue(
                                ExerciseActivityViewEvents.ShowToast(
                                    stringProvider.getString(
                                        R.string.error_loading_data
                                    )
                                )
                            )
                        }

                        currentStudy?.let {
                            onExerciseListItemSelected(selectedStudy = it)
                        } ?: showFirstExerciseOfCourse(courseId)
                    }

        }
    }

    sealed class ExerciseActivityViewEvents : ViewEvents {
        class SetCourseAndRenderUI(val courseList: List<Course>) : ExerciseActivityViewEvents()
        class ShowExerciseList(val exerciseList: List<Exercise>) : ExerciseActivityViewEvents()
        class ShowExerciseFragment(
            val currentStudy: CurrentStudy,
            val isFirst: Boolean,
            val isLast: Boolean,
            val isCompleted: Boolean
        ) : ExerciseActivityViewEvents()

        class MarkExerciseAsSelected(val currentStudy: CurrentStudy) : ExerciseActivityViewEvents()
        class MarkExerciseAsCompleted(val currentStudy: CurrentStudy) : ExerciseActivityViewEvents()
        class UpdateList(val exerciseList: List<Exercise>) : ExerciseActivityViewEvents()
        class ShowToast(val toastText: String) : ExerciseActivityViewEvents()
    }

    sealed class ExerciseActivityViewActions : ViewModelAction {
        data class FetchCourseExerciseDataWithCourse(
            val courseId: String,
        ) : ExerciseActivityViewActions()

        data class FetchCourseExercises(
            val courseId: String,
        ) : ExerciseActivityViewActions()

        data class PrevNavigationClicked(
            val currentStudy: CurrentStudy,
        ) : ExerciseActivityViewActions()

        data class NextNavigationClicked(
            val currentStudy: CurrentStudy,
        ) : ExerciseActivityViewActions()

        data class ExerciseListItemSelected(
            val currentStudy: CurrentStudy?,
            val selectedStudy: CurrentStudy,
        ) : ExerciseActivityViewActions()

        data class ExerciseMarkedCompleted(
            val currentStudy: CurrentStudy,
        ) : ExerciseActivityViewActions()

        data class FirstTimeLaunch(val courseId: String, val courseName: String) :
            ExerciseActivityViewActions()

    }

    data class ExerciseActivityViewState(
        val isLoading: Boolean = false,
    ) : ViewState

}