package org.navgurukul.learn.ui.learn.c4ca

import androidx.lifecycle.viewModelScope
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.merakilearn.core.utils.CorePreferences
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.ViewEvents
import org.navgurukul.commonui.platform.ViewModelAction
import org.navgurukul.commonui.platform.ViewState
import org.navgurukul.commonui.resources.StringProvider
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.*
import org.navgurukul.learn.courses.network.model.CompletedContentsIds
import org.navgurukul.learn.courses.repository.LearnRepo

class ModuleCourseContentViewModel(
    private val learnRepo: LearnRepo,
    private val caRepo: C4CARepo,
    corePreferences: CorePreferences,
    private val stringProvider: StringProvider,
    private val moduleId : Int,
    private val pathwayId : Int,
    private var courseId: String,
    private val contentId: String? = null,
) : BaseViewModel<ModuleCourseContentActivityViewEvents, ModuleCourseContentActivityViewState>(ModuleCourseContentActivityViewState())
{
    private lateinit var currentCourse: org.navgurukul.learn.courses.network.model.Course
    private var coursesList: List<org.navgurukul.learn.courses.network.model.Course>? = null
    private val selectedLanguage = corePreferences.selectedLanguage
    private var currentStudy: ModuleCurrentStudy? = null

    init {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val courses = caRepo.moduleGetCourseContentAsync(courseId, selectedLanguage)
            if (courses == null) {
                setState { copy(isLoading = false) }
                _viewEvents.postValue(
                    ModuleCourseContentActivityViewEvents.ShowToast(stringProvider.getString(R.string.error_loading_data))
                )
            } else {
                launchLastSelectedContentOfCourse(courses, contentId)
            }
        }
    }

    private fun updateCourseWIthCompletedContent(course: org.navgurukul.learn.courses.network.model.Course?, completedContentList: CompletedContentsIds) {
        completedContentList.assessments?.forEach { assessmentId ->
            markContentCompletedInCourse(course, assessmentId.toString())
        }
        completedContentList.exercises?.forEach { assessmentId ->
            markContentCompletedInCourse(course, assessmentId.toString())
        }
        completedContentList.classes?.forEach { assessmentId ->
            markContentCompletedInCourse(course, assessmentId.toString())
        }
    }

    private fun markContentCompletedInCourse(course: org.navgurukul.learn.courses.network.model.Course?, contentId: String) {
        course?.courseContents?.find { it.id == contentId }?.courseContentProgress = CourseContentProgress.COMPLETED
    }

    private suspend fun launchLastSelectedContentOfCourse(course: org.navgurukul.learn.courses.network.model.Course?, contentId: String? = null) {
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
                    learnRepo.fetchCurrentStudyForCourse(course.id.toString())     // NEED TO CHECK FOR DB
                }?.exerciseId ?: currentCourse.courseContents.first().id
                onContentListItemSelected(exerciseId)
            }
        }
    }

    fun handle(action: ModuleCourseContentActivityViewActions) {
        when (action) {
            is ModuleCourseContentActivityViewActions.ContentListItemSelected -> onContentListItemSelected(
                action.contentId
            )
            is ModuleCourseContentActivityViewActions.ContentMarkedCompleted -> onExerciseMarkedCompleted()
            is ModuleCourseContentActivityViewActions.NextNavigationClicked -> navigate(ModuleExerciseNavigation.NEXT)
            is ModuleCourseContentActivityViewActions.PrevNavigationClicked -> navigate(ModuleExerciseNavigation.PREV)
            is ModuleCourseContentActivityViewActions.OnNextCourseClicked -> launchNextCourse(currentCourse.id.toString())
        }
    }

    private fun navigate(navigation: ModuleExerciseNavigation) {
        if(::currentCourse.isInitialized) {
            val currentStudyIndex = currentCourse.courseContents.indexOfFirst {
                it.id == currentStudy?.exerciseId
            }
            val courseContentType = currentCourse.courseContents[currentStudyIndex].courseContentType
            if (navigation == ModuleExerciseNavigation.PREV && currentStudyIndex > 0) {
                onContentListItemSelected(
                    currentCourse.courseContents[currentStudyIndex - 1].id,
                    navigation
                )
            } else if (navigation == ModuleExerciseNavigation.NEXT && currentStudyIndex < currentCourse.courseContents.size - 1) {
                if (courseContentType == CourseContentType.exercise){
                    postLearningTrackStatus(currentCourse.courseContents[currentStudyIndex].id)
                }
                onContentListItemSelected(
                    currentCourse.courseContents[currentStudyIndex + 1].id,
                    navigation
                )

            } else if (navigation == ModuleExerciseNavigation.NEXT && currentStudyIndex == currentCourse.courseContents.size - 1) {
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
                postLearningTrackStatus(currentCourse.courseContents[currentStudyIndex].id)
            }
        }else{
            _viewEvents.setValue(ModuleCourseContentActivityViewEvents.ShowToast(stringProvider.getString(R.string.retry_after_some_time)))
        }
    }

    private fun onContentListItemSelected(
        contentId: String,
        navigation: ModuleExerciseNavigation? = null
    ) {
        currentStudy = ModuleCurrentStudy(currentCourse.id.toString(), contentId).apply {
            viewModelScope.launch(Dispatchers.IO) {
//                learnRepo.saveCourseContentCurrent(this@apply)
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
                    ModuleCourseContentActivityViewEvents.ShowModuleExerciseFragment(
                        isFirst,
                        isLast,
                        isCompleted,
                        currentCourse.id.toString(),
                        contentId,
                        courseContentType,
                        navigation
                    )
                )
            } else if (courseContentType == CourseContentType.class_topic) {
                _viewEvents.setValue(
                    ModuleCourseContentActivityViewEvents.ShowClassFragment(
                        isFirst,
                        isLast,
                        isCompleted,
                        currentCourse.id.toString(),
                        contentId,
                        courseContentType,
                        navigation
                    )
                )
            }else if (courseContentType == CourseContentType.assessment){
                _viewEvents.setValue(
                    ModuleCourseContentActivityViewEvents.ShowAssessmentFragment(
                        isFirst,
                        isLast,
                        isCompleted,
                        currentCourse.id.toString(),
                        contentId,
                        courseContentType,
                        navigation
                    )
                )
            }
        }else{
            _viewEvents.setValue(ModuleCourseContentActivityViewEvents.ShowToast(stringProvider.getString(R.string.content_error_message)))
        }
    }


    private fun onExerciseMarkedCompleted() {
//        markCourseExerciseCompletedInDb(currentStudy?.exerciseId)

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
        getNextCourse(currentCourseId.toInt())?.let {
            viewModelScope.launch {
                caRepo.moduleGetCourseContentAsync(it.id.toString(), selectedLanguage).let { course ->
                    //course with exercise details
                    launchLastSelectedContentOfCourse(it)
                }
            }
        } ?: run {
            _viewEvents.postValue(
                ModuleCourseContentActivityViewEvents.FinishActivity
            )
        }

    }


//    private fun markCourseExerciseCompletedInDb(
//        exerciseId: String?
//    ) {
//        exerciseId?.let {
//            viewModelScope.launch {
//                when(currentCourse.courseContents.find { it.id == exerciseId }?.courseContentType){
//                    CourseContentType.assessment -> learnRepo.markCourseAssessmentCompleted(it)
//                    CourseContentType.class_topic -> learnRepo.markCourseClassCompleted(it)
//                    CourseContentType.exercise -> learnRepo.markCourseExerciseCompleted(it)
//                }
//            }
//        }
//    }

    private fun getNextCourse(currentCourseId: Int): org.navgurukul.learn.courses.network.model.Course? {
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
            currentCourse.pathwayId?.let { learnRepo.postLearningTrackStatus(it, currentCourse.id.toString(), contentId)
            }
        }
    }

}


enum class ModuleExerciseNavigation { PREV, NEXT }

sealed class ModuleCourseContentActivityViewEvents : ViewEvents {

    class ShowModuleExerciseFragment(
        val isFirst: Boolean,
        val isLast: Boolean,
        val isCompleted: Boolean,
        val courseId: String,
        val contentId: String,
        val courseContentType: CourseContentType,
        val navigation: ModuleExerciseNavigation?
    ) : ModuleCourseContentActivityViewEvents()

    class ShowClassFragment(
        val isFirst: Boolean,
        val isLast: Boolean,
        val isCompleted: Boolean,
        val courseId: String,
        val contentId: String,
        val courseContentType: CourseContentType,
        val navigation: ModuleExerciseNavigation?
    ) : ModuleCourseContentActivityViewEvents()


    class ShowAssessmentFragment(
        val isFirst: Boolean,
        val isLast: Boolean,
        val isCompleted: Boolean,
        val courseId: String,
        val contentId: String,
        val courseContentType: CourseContentType,
        val navigation: ModuleExerciseNavigation?
    ) : ModuleCourseContentActivityViewEvents()



    object FinishActivity : ModuleCourseContentActivityViewEvents()
    class ShowToast(val toastText: String) : ModuleCourseContentActivityViewEvents()

}

data class ModuleCourseContentActivityViewState (
    val isLoading: Boolean = false,
    val isCourseCompleted: Boolean = false,
    val currentCourseTitle: String = "",
    val nextCourseTitle: String = "",
    val currentContentIndex: Int = 0,
    val courseContentList: List<CourseContents> = listOf(),
    ): ViewState


sealed class ModuleCourseContentActivityViewActions : ViewModelAction {
    object OnNextCourseClicked : ModuleCourseContentActivityViewActions()
    object PrevNavigationClicked : ModuleCourseContentActivityViewActions()
    object NextNavigationClicked : ModuleCourseContentActivityViewActions()
    data class ContentListItemSelected(val contentId: String) : ModuleCourseContentActivityViewActions()
    object ContentMarkedCompleted : ModuleCourseContentActivityViewActions()
}