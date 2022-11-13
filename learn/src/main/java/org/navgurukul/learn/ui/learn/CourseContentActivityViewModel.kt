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
import org.navgurukul.learn.courses.network.model.CompletedContentsIds

class CourseContentActivityViewModel(
    private val learnRepo: LearnRepo,
    corePreferences: CorePreferences,
    private val stringProvider: StringProvider,
    private var courseId: String,
    private val pathwayId: Int,
    private val contentId: String? = null,
) : BaseViewModel<CourseContentActivityViewEvents, CourseContentActivityViewState>(
    CourseContentActivityViewState()
) {

    private lateinit var currentCourse: Course
    private var coursesList: List<Course>? = null
    private val selectedLanguage = corePreferences.selectedLanguage
    private var currentStudy: CurrentStudy? = null

    init {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            combine(
                learnRepo.fetchCourseContentDataWithCourse(courseId, pathwayId, selectedLanguage),
                learnRepo.getCoursesDataByPathway(pathwayId, false),
                learnRepo.getCompletedContentsIds(courseId)
            ) { course, courseList, completedContentList ->
                updateCourseWIthCompletedContent(course, completedContentList)
                course to courseList
            }.collect {
                val course = it.first
                coursesList = it.second
                if (course == null) {
                    setState { copy(isLoading = false) }
                    _viewEvents.postValue(
                        CourseContentActivityViewEvents.ShowToast(stringProvider.getString(R.string.error_loading_data))
                    )
                } else {
                    launchLastSelectedContentOfCourse(course, contentId)
                }
            }

        }

    }

    private fun updateCourseWIthCompletedContent(course: Course?, completedContentList: CompletedContentsIds) {
        completedContentList?.assessments?.forEach { assessmentId ->
            markContentCompletedInCourse(course, assessmentId.toString())
        }
        completedContentList?.exercises?.forEach { assessmentId ->
            markContentCompletedInCourse(course, assessmentId.toString())
        }
        completedContentList?.classes?.forEach { assessmentId ->
            markContentCompletedInCourse(course, assessmentId.toString())
        }
    }

    private fun markContentCompletedInCourse(course: Course?, contentId: String) {
        course?.courseContents?.find { it.id == contentId }?.courseContentProgress = CourseContentProgress.COMPLETED
    }

    private suspend fun launchLastSelectedContentOfCourse(course: Course?, contentId: String? = null) {
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

            contentId?.let { onContentListItemSelected(it) } ?: kotlin.run {
                val exerciseId = withContext(Dispatchers.IO) {
                    learnRepo.fetchCurrentStudyForCourse(course.id)
                }?.exerciseId ?: currentCourse.courseContents.first().id
                onContentListItemSelected(exerciseId)
            }
        }
    }

    fun handle(action: CourseContentActivityViewActions) {
        when (action) {
            is CourseContentActivityViewActions.ContentListItemSelected -> onContentListItemSelected(
                action.contentId
            )
            is CourseContentActivityViewActions.ContentMarkedCompleted -> onExerciseMarkedCompleted()
            is CourseContentActivityViewActions.NextNavigationClicked -> navigate(ExerciseNavigation.NEXT)
            is CourseContentActivityViewActions.PrevNavigationClicked -> navigate(ExerciseNavigation.PREV)
            is CourseContentActivityViewActions.OnNextCourseClicked -> launchNextCourse(currentCourse.id)
        }
    }

    private fun navigate(navigation: ExerciseNavigation) {
        if(::currentCourse.isInitialized) {
            val currentStudyIndex = currentCourse.courseContents.indexOfFirst {
                it.id == currentStudy?.exerciseId
            }
            val courseContentType = currentCourse.courseContents[currentStudyIndex].courseContentType
            if (navigation == ExerciseNavigation.PREV && currentStudyIndex > 0) {
                onContentListItemSelected(
                    currentCourse.courseContents[currentStudyIndex - 1].id,
                    navigation
                )
            } else if (navigation == ExerciseNavigation.NEXT && currentStudyIndex < currentCourse.courseContents.size - 1) {
                if (courseContentType == CourseContentType.exercise){
                    postLearningTrackStatus(currentCourse.courseContents[currentStudyIndex].id)
                }
                onContentListItemSelected(
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
        }else{
            _viewEvents.setValue(CourseContentActivityViewEvents.ShowToast(stringProvider.getString(R.string.retry_after_some_time)))
        }
    }

    private fun onContentListItemSelected(
        contentId: String,
        navigation: ExerciseNavigation? = null
    ) {
        currentStudy = CurrentStudy(currentCourse.id, contentId).apply {
            viewModelScope.launch(Dispatchers.IO) {
                learnRepo.saveCourseContentCurrent(this@apply)
            }
        }
        markContentSelected(contentId)

        val index = currentCourse.courseContents.indexOfFirst { contentId == it.id }

        if(index > -1) {
            val isFirst = index == 0
            val isLast = index == currentCourse.courseContents.size - 1
            val isCompleted =
                currentCourse.courseContents[index].courseContentProgress == CourseContentProgress.COMPLETED
            val courseContentType = currentCourse.courseContents[index].courseContentType

            if (courseContentType == CourseContentType.exercise) {
                _viewEvents.setValue(
                    CourseContentActivityViewEvents.ShowExerciseFragment(
                        isFirst,
                        isLast,
                        isCompleted,
                        currentCourse.id,
                        contentId,
                        courseContentType,
                        navigation
                    )
                )
            } else if (courseContentType == CourseContentType.class_topic) {
                _viewEvents.setValue(
                    CourseContentActivityViewEvents.ShowClassFragment(
                        isFirst,
                        isLast,
                        isCompleted,
                        currentCourse.id,
                        contentId,
                        courseContentType,
                        navigation
                    )
                )
            }else if (courseContentType == CourseContentType.assessment){
                _viewEvents.setValue(
                    CourseContentActivityViewEvents.ShowAssessmentFragment(
                        isFirst,
                        isLast,
                        isCompleted,
                        currentCourse.id,
                        contentId,
                        courseContentType,
                        navigation
                    )
                )
            }
        }else{
            _viewEvents.setValue(CourseContentActivityViewEvents.ShowToast(stringProvider.getString(R.string.content_error_message)))
        }
    }

    private fun onExerciseMarkedCompleted() {
        markCourseExerciseCompletedInDb(currentStudy?.exerciseId)

        if(::currentCourse.isInitialized) {
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
                CourseContentActivityViewEvents.FinishActivity
            )
        }

    }



    private fun markCourseExerciseCompletedInDb(
        exerciseId: String?
    ) {
        exerciseId?.let {
            viewModelScope.launch {
                when(currentCourse.courseContents.find { it.id == exerciseId }?.courseContentType){
                    CourseContentType.assessment -> learnRepo.markCourseAssessmentCompleted(it)
                    CourseContentType.class_topic -> learnRepo.markCourseClassCompleted(it)
                    CourseContentType.exercise -> learnRepo.markCourseExerciseCompleted(it)
                }
            }
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
        var bool:Boolean=false;
        currentCourse.courseContents.toMutableList().let {
            it.forEachIndexed { index, content ->
                if (content.id == contentId) {
                    if (content.courseContentProgress != CourseContentProgress.COMPLETED) {
                        content.courseContentProgress = CourseContentProgress.IN_PROGRESS
                        selectedIndex = index
                    }
                    else if (content.courseContentProgress == CourseContentProgress.COMPLETED){
                        content.courseContentProgress = CourseContentProgress.COMPLETED_RESELECT
                        selectedIndex = index
                    }

                } else {
                    if (content.courseContentProgress == CourseContentProgress.IN_PROGRESS) {
                        content.courseContentProgress = CourseContentProgress.NOT_STARTED
                    }
                    else if (content.courseContentProgress == CourseContentProgress.COMPLETED_RESELECT){
                        content.courseContentProgress = CourseContentProgress.COMPLETED
                    }
                }
            }

            setState { copy(courseContentList = it, currentContentIndex = selectedIndex) }
        }
    }

    private fun postLearningTrackStatus(contentId: String){
        viewModelScope.launch {
                currentCourse.pathwayId?.let { learnRepo.postLearningTrackStatus(it, currentCourse.id, contentId)
            }
        }
        }


}

enum class ExerciseNavigation { PREV, NEXT }

sealed class CourseContentActivityViewEvents : ViewEvents {
    class ShowExerciseFragment(
        val isFirst: Boolean,
        val isLast: Boolean,
        val isCompleted: Boolean,
        val courseId: String,
        val contentId: String,
        val courseContentType: CourseContentType,
        val navigation: ExerciseNavigation?
    ) : CourseContentActivityViewEvents()

    class ShowClassFragment(
        val isFirst: Boolean,
        val isLast: Boolean,
        val isCompleted: Boolean,
        val courseId: String,
        val contentId: String,
        val courseContentType: CourseContentType,
        val navigation: ExerciseNavigation?
    ) : CourseContentActivityViewEvents()


    class ShowAssessmentFragment(
        val isFirst: Boolean,
        val isLast: Boolean,
        val isCompleted: Boolean,
        val courseId: String,
        val contentId: String,
        val courseContentType: CourseContentType,
        val navigation: ExerciseNavigation?
    ) : CourseContentActivityViewEvents()



    object FinishActivity : CourseContentActivityViewEvents()
    class ShowToast(val toastText: String) : CourseContentActivityViewEvents()
}

data class CourseContentActivityViewState(
    val isLoading: Boolean = false,
    val isCourseCompleted: Boolean = false,
    val currentCourseTitle: String = "",
    val nextCourseTitle: String = "",
    val currentContentIndex: Int = 0,
    val courseContentList: List<CourseContents> = listOf(),
) : ViewState

sealed class CourseContentActivityViewActions : ViewModelAction {
    object OnNextCourseClicked : CourseContentActivityViewActions()
    object PrevNavigationClicked : CourseContentActivityViewActions()
    object NextNavigationClicked : CourseContentActivityViewActions()
    data class ContentListItemSelected(val contentId: String) : CourseContentActivityViewActions()
    object ContentMarkedCompleted : CourseContentActivityViewActions()
}
