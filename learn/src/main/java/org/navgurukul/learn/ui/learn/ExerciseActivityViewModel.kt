package org.navgurukul.learn.ui.learn

import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
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
) : BaseViewModel<ExerciseActivityViewModel.ExerciseActivityViewEvents, ExerciseActivityViewModel.ExerciseActivityViewState>(
    ExerciseActivityViewState()
) {

    private var currentCourseExerciseList: List<Exercise>? = null
    private var fetchCourseExerciseJob: Job? = null
    private var fetchCourseExerciseWithCourseJob: Job? = null
    private val selectedLanguage = learnPreferences.selectedLanguage

    init {
        fetchCourseExerciseData(courseId)
        fetchCourseExerciseDataWithCourse(courseId)
    }

    fun handle(action: ExerciseActivityViewActions) {
        when (action) {
            is ExerciseActivityViewActions.FetchCourseExercises -> fetchCourseExerciseData(
                action.courseId,
            )
            is ExerciseActivityViewActions.FetchCourseExerciseDataWithCourse -> fetchCourseExerciseDataWithCourse(
                action.courseId,
            )
            is ExerciseActivityViewActions.ExerciseListItemSelected -> {
                onExerciseListItemSelected(
                    action.selectedStudy,
                )
//                markExerciseSelected(action.selectedStudy)
//
//                action.currentStudy?.let { markExerciseUnselected(it) }
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
        }
    }

    private fun markExerciseUnselected(currenStudy: CurrentStudy) {
        currentCourseExerciseList?.let { list ->
            val updatedList = list.toMutableList()

            val currentExercise = updatedList.find {
                currenStudy.exerciseId == it.id
            }
            currentExercise?.let {
                if(it.exerciseProgress == ExerciseProgress.IN_PROGRESS){
                    it.exerciseProgress = ExerciseProgress.NOT_STARTED
                }
                onExerciseListUpdated(updatedList)
            }
        }

    }

    private fun onExerciseListUpdated(updatedList: MutableList<Exercise>) {
        currentCourseExerciseList = updatedList
        _viewEvents.postValue(ExerciseActivityViewEvents.UpdateList(updatedList))
    }

    private fun onNextListItemRequested(currentStudy: CurrentStudy) {
        currentCourseExerciseList?.let { list ->
            val currentStudyIndex = list.indexOfFirst {
                it.id == currentStudy.exerciseId
            }
            if (currentStudyIndex < list.size - 1) {
                val nextExercise = list[currentStudyIndex + 1]
                val nextStudy = CurrentStudy(
                    courseId, nextExercise.courseName, nextExercise.slug, nextExercise.name, nextExercise.id
                )
                onExerciseListItemSelected(nextStudy)
//                markExerciseUnselected(currentStudy)
            }
        }
    }

    private fun onPrevListItemRequested(currentStudy: CurrentStudy) {
        currentCourseExerciseList?.let { list ->
            val currentStudyIndex = list.indexOfFirst {
                it.id == currentStudy.exerciseId
            }
            if (currentStudyIndex > 1) {
                val nextExercise = list[currentStudyIndex - 1]
                val nextStudy = CurrentStudy(
                    courseId, nextExercise.courseName, nextExercise.slug, nextExercise.name, nextExercise.id
                )
                onExerciseListItemSelected(nextStudy)
//                markExerciseUnselected(currentStudy)
            }
        }
    }

    private fun onExerciseListItemSelected(selectedStudy: CurrentStudy) {
        currentCourseExerciseList?.let { list ->
             val index = list.indexOfFirst { selectedStudy.exerciseId == it.id }
            val isFirst = if(index == 0) true else false
            val isLast = if(index == list.size - 1) true else false
            _viewEvents.postValue(ExerciseActivityViewEvents.ShowExerciseFragment(selectedStudy, isFirst, isLast))
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
                if(it.exerciseProgress != ExerciseProgress.COMPLETED){
                    it.exerciseProgress = ExerciseProgress.IN_PROGRESS
                }
                onExerciseListUpdated(updatedList)
            }
        }
//        _viewEvents.postValue(ExerciseActivityViewEvents.MarkExerciseAsSelected(selectedStudy))
    }

    fun fetchCourseExerciseData(courseId: String){
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

    fun fetchCourseExerciseDataWithCourse(courseId: String){
        fetchCourseExerciseWithCourseJob?.cancel()
        fetchCourseExerciseWithCourseJob = viewModelScope.launch {
            setState { copy(isLoading = true) }
            val response =
                learnRepo.fetchCourseExerciseDataWithCourse(courseId, selectedLanguage)
                    .collect {
                        val list = it

                        setState { copy(isLoading = false) }

                        if (list != null) {
                            _viewEvents.postValue(ExerciseActivityViewEvents.SetCourseAndRenderUI(list))
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

    sealed class ExerciseActivityViewEvents : ViewEvents {
        class SetCourseAndRenderUI(val courseList: List<Course>) : ExerciseActivityViewEvents()
        class ShowExerciseList(val exerciseList: List<Exercise>) : ExerciseActivityViewEvents()
        class ShowExerciseFragment(val currentStudy: CurrentStudy, val isFirst: Boolean, val isLast: Boolean)
            : ExerciseActivityViewEvents()
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

    }

    data class ExerciseActivityViewState(
        val isLoading: Boolean = false,
    ) : ViewState

}