package org.navgurukul.learn.ui.learn.c4ca

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.ViewEvents
import org.navgurukul.commonui.platform.ViewState
import org.navgurukul.learn.courses.network.model.Module

class C4CAFragmentViewModel(
    private val c4caRepo: C4CARepo
) : BaseViewModel<C4CAFragmentViewEvents, C4CAFragmentViewState>(C4CAFragmentViewState()) {


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
                        moduleList = it.modules!!,
                    )
                }
                _viewEvents.postValue(C4CAFragmentViewEvents.GetC4CAPathways(it.modules!!))
            }
        }
    }
}

data class C4CAFragmentViewState(
    val moduleList: List<Module> = arrayListOf(),
) : ViewState

sealed class C4CAFragmentViewEvents : ViewEvents {
    data class GetC4CAPathways(val C4CA: List<Module>) : C4CAFragmentViewEvents()
}