package org.navgurukul.learn.ui.learn


import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.ViewEvents
import org.navgurukul.commonui.platform.ViewModelAction
import org.navgurukul.commonui.platform.ViewState
import org.navgurukul.commonui.resources.StringProvider
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.CourseClassContent
import org.navgurukul.learn.courses.db.models.displayableLanguage
import org.navgurukul.learn.courses.db.models.sanitizedType
import org.navgurukul.learn.courses.repository.LearnRepo
import org.navgurukul.learn.util.ColorProvider
import org.navgurukul.learn.util.toDisplayableInterval
import java.util.*
import java.util.concurrent.TimeUnit

class EnrollViewModel(
    private val stringProvider: StringProvider,
    private val colorProvider: ColorProvider,
    private val learnRepo: LearnRepo,
) : BaseViewModel<EnrollViewEvents, EnrollViewState>(EnrollViewState()) {

    fun handle(viewActions: EnrollViewActions) {
        when (viewActions) {
            is EnrollViewActions.PrimaryAction -> primaryAction(viewActions.mClass, viewActions.shouldRegisterUnregisterAll)
            is EnrollViewActions.RequestPageLoad -> loadPage(viewActions.mClass)
            is EnrollViewActions.DropOut -> dropOut(viewActions.mClass, viewActions.shouldRegisterUnregisterAll)

        }
    }

    private fun loadPage(mClass: CourseClassContent) {

        setState { copy(isLoading = true) }
        viewModelScope.launch {
//            classes = learnRepo.fetchClassData(classId)
            mClass.let {

                val durationToClassStart = (it.startTime.time - Date().time)
                var primaryActionBackgroundColor =
                    colorProvider.getColorFromAttribute(R.attr.colorPrimary)
                val primaryAction = if (mClass.isEnrolled) {
                    if (classJoinEnabled(durationToClassStart)) {
                        stringProvider.getString(R.string.join_type_class, it.sanitizedType())
                    } else {
                        primaryActionBackgroundColor =
                            colorProvider.getColorFromAttribute(R.attr.colorBackgroundDisabled)
                        stringProvider.getString(
                            R.string.starts_in,
                            durationToClassStart.toDisplayableInterval(stringProvider)
                        )
                    }
                } else {
                    stringProvider.getString(R.string.enroll_to_type_class, it.sanitizedType())
                }

                val teacherName =
                    it.facilitator?.name ?: stringProvider.getString(R.string.unavailable)

                val language = it.displayableLanguage()
                setState {
                    copy(
                        isLoading = false,
                        showError = false,
                        primaryAction = primaryAction,
                        menuId = menuId,
                        about = it.description,
                        teacherDetail = teacherName,
                        title = it.title,
                        type = it.sanitizedType(),
                        language = language,
                        primaryActionBackgroundColor = primaryActionBackgroundColor,
                        isEnrolled = it.isEnrolled
                    )
                }
            }
        }
    }
    private fun dropOut(mClass: CourseClassContent, shouldRegisterUnregisterAll: Boolean = false){
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val result = learnRepo.enrollToClass(mClass.id.toInt(), true, shouldRegisterUnregisterAll)
            setState { copy(isLoading = true) }
            if (result){
                mClass.isEnrolled = false
                setState {
                    copy(
                        isLoading = false,
                        primaryAction = stringProvider.getString(R.string.enroll_to_class),
                        menuId = null,
                        primaryActionBackgroundColor = colorProvider.getColorFromAttribute(R.attr.colorPrimary)
                    )
                }
                _viewEvents.setValue(EnrollViewEvents.RefreshContent(mClass))
                _viewEvents.setValue(EnrollViewEvents.ShowToast(stringProvider.getString(R.string.log_out_class)))
            } else {
                setState { copy(isLoading = false) }
                _viewEvents.setValue(EnrollViewEvents.ShowToast(stringProvider.getString(R.string.unable_to_drop)))
            }
        }
    }

    private fun primaryAction(mClass: CourseClassContent, shouldRegisterUnregisterAll: Boolean = false) {
        viewModelScope.launch {
            val classes = mClass
            if (mClass.isEnrolled) {
                val durationToClassStart = (classes.startTime.time - Date().time)
                if (classJoinEnabled(durationToClassStart)) {
                    classes.meetLink?.let {
                        _viewEvents.setValue(EnrollViewEvents.OpenLink(it))
                    }
                } else {
                    _viewEvents.setValue(
                        EnrollViewEvents.ShowToast(
                            stringProvider.getString(
                                R.string.class_not_started_toast,
                                durationToClassStart.toDisplayableInterval(stringProvider)
                            )
                        )
                    )
                }
            } else {
                setState { copy(isLoading = true) }
                val result = learnRepo.enrollToClass(mClass.id.toInt(), false, shouldRegisterUnregisterAll)
                if (result) {
                    mClass.isEnrolled = true

                    val durationToClassStart = (classes.startTime.time - Date().time)
                    var primaryActionBackgroundColor =
                        colorProvider.getColorFromAttribute(R.attr.colorPrimary)
                    val primaryAction = if (classJoinEnabled(durationToClassStart)) {
                        stringProvider.getString(R.string.join_type_class, mClass.sanitizedType())
                    } else {
                        primaryActionBackgroundColor =
                            colorProvider.getColorFromAttribute(R.attr.colorBackgroundDisabled)
                        stringProvider.getString(
                            R.string.starts_in,
                            durationToClassStart.toDisplayableInterval(stringProvider)
                        )
                    }

                    setState {
                        copy(
                            isLoading = false,
                            primaryAction = primaryAction,
                            primaryActionBackgroundColor = primaryActionBackgroundColor
                        )
                    }
                    _viewEvents.setValue(EnrollViewEvents.RefreshContent(mClass))
                    _viewEvents.setValue(EnrollViewEvents.ShowToast(stringProvider.getString(R.string.enroll_to_class_msg)))
                } else {
                    setState { copy(isLoading = false) }
                    _viewEvents.setValue(EnrollViewEvents.ShowToast(stringProvider.getString(R.string.unable_to_enroll)))
                }
            }
        }
    }

    private fun classJoinEnabled(durationToClassStart: Long) =
        durationToClassStart < TimeUnit.MINUTES.toMillis(5)

}

sealed class EnrollViewEvents : ViewEvents {
    class ShowToast(val toastText: String) : EnrollViewEvents()
    class OpenLink(val link: String) : EnrollViewEvents()
    class RefreshContent(val mClass: CourseClassContent): EnrollViewEvents()
}

sealed class EnrollViewActions : ViewModelAction {
   data class PrimaryAction(val mClass: CourseClassContent, val shouldRegisterUnregisterAll: Boolean): EnrollViewActions()
   data class RequestPageLoad(val mClass: CourseClassContent): EnrollViewActions()
    data class DropOut(val mClass: CourseClassContent, val shouldRegisterUnregisterAll: Boolean) : EnrollViewActions()
}

data class EnrollViewState(
    val isLoading: Boolean = false,
    val showError: Boolean = false,
    val primaryAction: String? = null,
    val primaryActionBackgroundColor: Int? = null,
    val type: String? = null,
    val about: String? = null,
    val teacherDetail: String? = null,
    val details: String? = null,
    val rules: String? = null,
    val title: String? = null,
    val language: String? = null,
    val menuId: Int? = null,
    val isRevisionClassEligible : Boolean = false,
    val isEnrolled: Boolean = false,
) : ViewState