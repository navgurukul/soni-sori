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

class EnrollViewModel(private val classId: Int,
                      private val isEnrolled: Boolean,
                      private val stringProvider: StringProvider,
                      private val applicationRepo: ApplicationRepo
): BaseViewModel<EnrollViewEvents, EnrollViewState>(EnrollViewState()) {

    init {
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            val classes = applicationRepo.fetchClassData(classId)
            classes?.let {
                val buttonText = if (isEnrolled) {
                    stringProvider.getString(R.string.drop_out)
                } else {
                    stringProvider.getString(R.string.enroll_to_class)
                }
                val teacherName =  classes.facilitator?.name ?: stringProvider.getString(R.string.unavailable)
                val date = "${classes.startTime.toDate()}. (${classes.startTime.toDay()})"
                val time = "${classes.startTime.toTime()} - ${classes.endTime.toTime()}"
                val dateAndTiming = stringProvider.getString(R.string.enroll_date_and_time, teacherName, date, time)
                setState { copy(isLoading = false, showError = false, enrollButton = buttonText,
                    about = classes.description, details = dateAndTiming, rules = classes.rules?.en, title = classes.title, type = classes.sanitizedType()) }
            } ?: run {
                setState { copy(isLoading = false, showError = true) }
            }
        }
    }

    fun handle(viewActions: EnrollViewActions) {
        when (viewActions) {
            is EnrollViewActions.EnrollToClass -> enrollToClass()
        }
    }

    private fun enrollToClass() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val result = applicationRepo.enrollToClass(classId, isEnrolled)
            setState { copy(isLoading = false) }
            if (isEnrolled) {
                if (result) {
                    _viewEvents.setValue(EnrollViewEvents.ShowToast(stringProvider.getString(R.string.log_out_class)))
                    _viewEvents.setValue(EnrollViewEvents.CloseScreen)
                } else {
                    _viewEvents.setValue(EnrollViewEvents.ShowToast(stringProvider.getString(R.string.unable_to_drop)))
                }
            } else {
                if (result) {
                    _viewEvents.setValue(EnrollViewEvents.ShowToast(stringProvider.getString(R.string.enrolled)))
                    _viewEvents.setValue(EnrollViewEvents.CloseScreen)
                } else {
                    _viewEvents.setValue(EnrollViewEvents.ShowToast(stringProvider.getString(R.string.unable_to_enroll)))
                }
            }
        }
    }

}

sealed class EnrollViewEvents : ViewEvents {
    object CloseScreen: EnrollViewEvents()
    class ShowToast(val toastText: String) : EnrollViewEvents()
}

sealed class EnrollViewActions : ViewModelAction {
    object EnrollToClass : EnrollViewActions()
}

data class EnrollViewState(
    val isLoading: Boolean = false,
    val showError: Boolean = false,
    val enrollButton: String? = null,
    val type: String? = null,
    val about: String? = null,
    val details: String? = null,
    val rules: String? = null,
    val title: String? = null
) : ViewState
