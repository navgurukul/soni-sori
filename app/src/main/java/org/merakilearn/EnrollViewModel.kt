package org.merakilearn

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.merakilearn.datasource.ClassesRepo
import org.merakilearn.datasource.network.model.Classes
import org.merakilearn.datasource.network.model.displayableLanguage
import org.merakilearn.datasource.network.model.sanitizedType
import org.merakilearn.util.toDate
import org.merakilearn.util.toDay
import org.merakilearn.util.toDisplayableInterval
import org.merakilearn.util.toTime
import org.navgurukul.chat.core.resources.ColorProvider
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.ViewEvents
import org.navgurukul.commonui.platform.ViewModelAction
import org.navgurukul.commonui.platform.ViewState
import org.navgurukul.commonui.resources.StringProvider
import java.util.*
import java.util.concurrent.TimeUnit

class EnrollViewModel(
    private val classId: Int,
    private var isEnrolled: Boolean,
    private val stringProvider: StringProvider,
    private val colorProvider: ColorProvider,
    private val classesRepo: ClassesRepo
) : BaseViewModel<EnrollViewEvents, EnrollViewState>(EnrollViewState()) {

    private var classes: Classes? = null

    init {
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            classes = classesRepo.fetchClassData(classId)
            classes?.let {

                val durationToClassStart = (it.startTime.time - Date().time)
                var primaryActionBackgroundColor =
                    colorProvider.getColorFromAttribute(R.attr.colorPrimary)
                val primaryAction = if (isEnrolled) {
                    if (classJoinEnabled(durationToClassStart)) {
                        stringProvider.getString(R.string.join_class)
                    } else {
                        primaryActionBackgroundColor =
                            colorProvider.getColorFromAttribute(R.attr.colorBackgroundDisabled)
                        stringProvider.getString(
                            R.string.join_class_in,
                            durationToClassStart.toDisplayableInterval(stringProvider)
                        )
                    }
                } else {
                    stringProvider.getString(R.string.enroll_to_class)
                }

                val menuId = if (isEnrolled) {
                    R.menu.enroll_menu
                } else {
                    null
                }

                val teacherName =
                    it.facilitator?.name ?: stringProvider.getString(R.string.unavailable)
                val date = "${it.startTime.toDate()}. (${it.startTime.toDay()})"
                val time = "${it.startTime.toTime()} - ${it.endTime.toTime()}"
                val dateAndTiming =
                    stringProvider.getString(R.string.enroll_date_and_time, teacherName, date, time)

                val language = it.displayableLanguage()
                setState {
                    copy(
                        isLoading = false,
                        showError = false,
                        primaryAction = primaryAction,
                        menuId = menuId,
                        about = it.description,
                        details = dateAndTiming,
                        rules = it.rules?.en,
                        title = it.title,
                        type = it.sanitizedType(),
                        language = language,
                        primaryActionBackgroundColor = primaryActionBackgroundColor
                    )
                }
            } ?: run {
                setState { copy(isLoading = false, showError = true) }
            }
        }
    }

    fun handle(viewActions: EnrollViewActions) {
        when (viewActions) {
            is EnrollViewActions.PrimaryAction -> primaryAction()
            EnrollViewActions.DropOut -> dropOut()
        }
    }

    private fun dropOut() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val result = classesRepo.enrollToClass(classId, true)
            setState { copy(isLoading = false) }
            if (result) {
                isEnrolled = false
                setState {
                    copy(
                        isLoading = false,
                        primaryAction = stringProvider.getString(R.string.enroll_to_class),
                        menuId = null,
                        primaryActionBackgroundColor = colorProvider.getColorFromAttribute(R.attr.colorPrimary)
                    )
                }
                _viewEvents.setValue(EnrollViewEvents.ShowToast(stringProvider.getString(R.string.log_out_class)))
            } else {
                setState { copy(isLoading = false) }
                _viewEvents.setValue(EnrollViewEvents.ShowToast(stringProvider.getString(R.string.unable_to_drop)))
            }
        }
    }

    private fun primaryAction() {
        viewModelScope.launch {
            val classes = classes ?: return@launch
            if (isEnrolled) {
                val durationToClassStart = (classes.startTime.time - Date().time)
                if (classJoinEnabled(durationToClassStart)) {
                    _viewEvents.setValue(EnrollViewEvents.OpenLink(classes.meetLink))
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
                val result = classesRepo.enrollToClass(classId, false)
                if (result) {
                    isEnrolled = true

                    val durationToClassStart = (classes.startTime.time - Date().time)
                    var primaryActionBackgroundColor =
                        colorProvider.getColorFromAttribute(R.attr.colorPrimary)
                    val primaryAction = if (classJoinEnabled(durationToClassStart)) {
                        stringProvider.getString(R.string.join_class)
                    } else {
                        primaryActionBackgroundColor =
                            colorProvider.getColorFromAttribute(R.attr.colorBackgroundDisabled)
                        stringProvider.getString(
                            R.string.join_class_in,
                            durationToClassStart.toDisplayableInterval(stringProvider)
                        )
                    }

                    setState {
                        copy(
                            isLoading = false,
                            primaryAction = primaryAction,
                            menuId = R.menu.enroll_menu,
                            primaryActionBackgroundColor = primaryActionBackgroundColor
                        )
                    }
                    _viewEvents.setValue(EnrollViewEvents.ShowToast(stringProvider.getString(R.string.enrolled)))
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
}

sealed class EnrollViewActions : ViewModelAction {
    object PrimaryAction : EnrollViewActions()
    object DropOut : EnrollViewActions()
}

data class EnrollViewState(
    val isLoading: Boolean = false,
    val showError: Boolean = false,
    val primaryAction: String? = null,
    val primaryActionBackgroundColor: Int? = null,
    val type: String? = null,
    val about: String? = null,
    val details: String? = null,
    val rules: String? = null,
    val title: String? = null,
    val language: String? = null,
    val menuId: Int? = null
) : ViewState
