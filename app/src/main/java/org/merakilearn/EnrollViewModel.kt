package org.merakilearn

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.merakilearn.datasource.ApplicationRepo
import org.merakilearn.datasource.network.model.sanitizedType
import org.merakilearn.util.toDate
import org.merakilearn.util.toDay
import org.merakilearn.util.toTime
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.ViewEvents
import org.navgurukul.commonui.platform.ViewModelAction
import org.navgurukul.commonui.platform.ViewState
import org.navgurukul.commonui.resources.StringProvider

class EnrollViewModel(
    private val classId: Int,
    private var isEnrolled: Boolean,
    private val stringProvider: StringProvider,
    private val applicationRepo: ApplicationRepo
) : BaseViewModel<EnrollViewEvents, EnrollViewState>(EnrollViewState()) {

    private lateinit var meetLink: String

    init {
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            val classes = applicationRepo.fetchClassData(classId)
            classes?.let {
                val primaryAction = if (isEnrolled) {
                    stringProvider.getString(R.string.join_class)
                } else {
                    stringProvider.getString(R.string.enroll_to_class)
                }
                val secondaryAction = if (isEnrolled) {
                    stringProvider.getString(R.string.drop_out)
                } else {
                    null
                }
                val teacherName =
                    classes.facilitator?.name ?: stringProvider.getString(R.string.unavailable)
                val date = "${classes.startTime.toDate()}. (${classes.startTime.toDay()})"
                val time = "${classes.startTime.toTime()} - ${classes.endTime.toTime()}"
                val dateAndTiming =
                    stringProvider.getString(R.string.enroll_date_and_time, teacherName, date, time)
                meetLink = classes.meetLink
                setState {
                    copy(
                        isLoading = false,
                        showError = false,
                        primaryAction = primaryAction,
                        secondaryAction = secondaryAction,
                        about = classes.description,
                        details = dateAndTiming,
                        rules = classes.rules?.en,
                        title = classes.title,
                        type = classes.sanitizedType()
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
            EnrollViewActions.SecondaryAction -> secondaryAction()
        }
    }

    private fun secondaryAction() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val result = applicationRepo.enrollToClass(classId, true)
            setState { copy(isLoading = false) }
            if (result) {
                isEnrolled = false
                setState {
                    copy(
                        isLoading = false,
                        primaryAction = stringProvider.getString(R.string.enroll_to_class),
                        secondaryAction = null
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
            if (isEnrolled) {
                _viewEvents.setValue(EnrollViewEvents.OpenLink(meetLink))
            } else {
                setState { copy(isLoading = true) }
                val result = applicationRepo.enrollToClass(classId, false)
                if (result) {
                    isEnrolled = true
                    setState {
                        copy(
                            isLoading = false,
                            primaryAction = stringProvider.getString(R.string.join_class),
                            secondaryAction = stringProvider.getString(R.string.drop_out)
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

}

sealed class EnrollViewEvents : ViewEvents {
    class ShowToast(val toastText: String) : EnrollViewEvents()
    class OpenLink(val link: String) : EnrollViewEvents()
}

sealed class EnrollViewActions : ViewModelAction {
    object PrimaryAction : EnrollViewActions()
    object SecondaryAction : EnrollViewActions()
}

data class EnrollViewState(
    val isLoading: Boolean = false,
    val showError: Boolean = false,
    val primaryAction: String? = null,
    val secondaryAction: String? = null,
    val type: String? = null,
    val about: String? = null,
    val details: String? = null,
    val rules: String? = null,
    val title: String? = null
) : ViewState
