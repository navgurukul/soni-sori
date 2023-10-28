package org.navgurukul.learn.ui.learn.c4ca

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.ViewEvents
import org.navgurukul.commonui.platform.ViewState
import org.navgurukul.learn.courses.network.model.PathwayC4CA
import java.nio.file.Path

class C4CAFragmentViewModel(
    private val c4caRepo: C4CARepo
) : BaseViewModel<C4CAFragmentViewEvents, C4CAFragmentViewState>(C4CAFragmentViewState()) {

//    private val pathwayC4CA: PathwayC4CA

    init {
        viewModelScope.launch {
            getC4CAPathways()
        }
    }

    fun getC4CAPathways() {
        viewModelScope.launch {
            //setState { copy(loading = true) }
            val pathwaysC4CA = c4caRepo.getPathwayC4CA()
            pathwaysC4CA.let {
                setState {
                    copy(
                        pathwaysC4CA = it,
                    )
                }
                _viewEvents.postValue(C4CAFragmentViewEvents.OpenC4CAHomeFragment)
            }
        }
    }
}

data class C4CAFragmentViewState(
    var pathwaysC4CA: PathwayC4CA? = null,
) : ViewState

sealed class C4CAFragmentViewEvents : ViewEvents {
    object OpenC4CAHomeFragment : C4CAFragmentViewEvents()
}