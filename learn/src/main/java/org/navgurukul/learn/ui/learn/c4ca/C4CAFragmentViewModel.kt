package org.navgurukul.learn.ui.learn.c4ca

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.merakilearn.core.datasource.model.Language
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.ViewEvents
import org.navgurukul.commonui.platform.ViewState
import org.navgurukul.learn.courses.db.models.Course
import org.navgurukul.learn.courses.db.models.CourseClassContent
import org.navgurukul.learn.courses.db.models.Pathway
import org.navgurukul.learn.courses.network.model.Batch
import org.navgurukul.learn.courses.network.model.Module
import org.navgurukul.learn.courses.network.model.PathwayC4CA

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
                        pathways = it.modules!!,
                    )
                }
                _viewEvents.postValue(C4CAFragmentViewEvents.FetchC4CAPathways)
            }
        }
    }
}

//data class C4CAFragmentViewState(
//    var pathwaysC4CA: PathwayC4CA? = null,
//) : ViewState

data class C4CAFragmentViewState(
    val pathways: List<Module> = arrayListOf(),
) : ViewState

sealed class C4CAFragmentViewEvents : ViewEvents {
    object FetchC4CAPathways : C4CAFragmentViewEvents()
}