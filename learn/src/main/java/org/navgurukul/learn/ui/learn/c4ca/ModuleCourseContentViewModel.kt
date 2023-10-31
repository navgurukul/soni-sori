package org.navgurukul.learn.ui.learn.c4ca

import org.koin.core.KoinApplication.Companion.init
import org.merakilearn.core.utils.CorePreferences
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.ViewEvents
import org.navgurukul.commonui.platform.ViewState
import org.navgurukul.commonui.resources.StringProvider
import org.navgurukul.learn.courses.repository.LearnRepo

class ModuleCourseContentViewModel(
    private val learnRepo: LearnRepo,
    corePreferences: CorePreferences,
    private val stringProvider: StringProvider,
    private val moduleId : Int,
    private var courseId: String,
    private val pathwayId: Int,
    private val contentId: String? = null,
) : BaseViewModel<ModuleCourseContentActivityViewEvents, ModuleCourseContentActivityViewState>(ModuleCourseContentActivityViewState())
{

}

sealed class ModuleCourseContentActivityViewEvents : ViewEvents {

}

data class ModuleCourseContentActivityViewState (
    val loading: Boolean = false,


    ): ViewState
