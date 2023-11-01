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

     private fun getC4CAPathways() {
        viewModelScope.launch {
            try {
                val pathwaysC4CA = c4caRepo.getPathwayC4CA()
                pathwaysC4CA.let {
                    setState {
                        copy(
                            moduleList = it.modules!!,
                            subtitle = it.name,
                            logo = it.logo,
                        )
                    }
                    _viewEvents.postValue(C4CAFragmentViewEvents.GetC4CAPathways(it.modules!!))
                }
            }
            catch (e: Exception) {
                _viewEvents.postValue(C4CAFragmentViewEvents.ShowToast(e.message.toString()))
            }
        }
    }

    private fun moduleGetCourseContentAsync(){

    }

    fun selectCourse(course: org.navgurukul.learn.courses.network.model.Course) {
        course.let {
            _viewEvents.postValue(
                C4CAFragmentViewEvents.OpenModuleCourseDetailActivity(course.id, course.name, course)
            )
        }
    }
}

data class C4CAFragmentViewState(
    val moduleList: List<Module> = arrayListOf(),
    val loading: Boolean = false,
    val subtitle: String? = null,
    val logo: String? = null,
    val code: String? = null,
) : ViewState

sealed class C4CAFragmentViewEvents : ViewEvents {
    data class ShowToast(val toastText: String)  : C4CAFragmentViewEvents()
    data class GetC4CAPathways(val C4CA: List<Module>) : C4CAFragmentViewEvents()
    data class OpenModuleCourseDetailActivity(
        val courseId: Int,
        val courseName: String,
        val course: org.navgurukul.learn.courses.network.model.Course
    ) : C4CAFragmentViewEvents()
}