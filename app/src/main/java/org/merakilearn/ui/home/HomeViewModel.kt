package org.merakilearn.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.merakilearn.datasource.ClassesRepo
import org.merakilearn.core.datasource.Config
import org.merakilearn.datasource.network.model.Classes
import org.merakilearn.core.datasource.model.Language
import org.merakilearn.util.relativeDay
import org.merakilearn.util.toDate
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.EmptyViewEvents
import org.navgurukul.commonui.platform.ViewModelAction
import org.navgurukul.commonui.platform.ViewState
import org.navgurukul.commonui.resources.StringProvider

class HomeViewModel(
    private val classesRepo: ClassesRepo,
    private val stringProvider: StringProvider,
    config: Config
) :
    BaseViewModel<EmptyViewEvents, HomeViewState>(HomeViewState()) {

    private var currentQuery: String? = null
    private var selectedLanguageCode: String? = null
    private var onlyEnrolled: Boolean = false

    val supportedLanguages =
        MutableLiveData<List<Language>>(config.getObjectifiedList(Config.KEY_AVAILABLE_LANG))

    init {
        viewModelScope.launch {
            classesRepo.updateClasses()
            classesRepo.classesFlow.collect {
                it?.let {
                    setState {
                        val items = it.toClassesData()
                        val emptyData = items.isEmpty()
                        copy(
                            isLoading = false,
                            searchEnabled = !emptyData,
                            showError = false,
                            showNoContent = emptyData,
                            itemList = items
                        )
                    }
                } ?: run {
                    setState { copy(isLoading = false, searchEnabled = false, showError = true) }
                }
            }
        }
    }

    fun handle(action: HomeViewActions) {
        when (action) {
            is HomeViewActions.Query -> {
                currentQuery = action.query
                filterClasses()
            }
            is HomeViewActions.FilterLanguage -> {
                selectedLanguageCode = action.langCode
                filterClasses()
            }
            is HomeViewActions.FilterEnrolled -> {
                onlyEnrolled = action.enrolledOnly
                filterClasses()
            }
        }
    }

    private fun filterClasses() {
        val classes = classesRepo.lastUpdatedClasses ?: return
        viewModelScope.launch(Dispatchers.Default) {
            val filteredClasses = classes.filter {
                val filterEnrolled = it.enrolled || !onlyEnrolled

                val filterQuery = currentQuery?.let { currentQuery ->
                    if (currentQuery.isNotEmpty()) {
                        val wordsToCompare = it.title.split(" ") + it.type.split("_")
                        wordsToCompare.find { word ->
                            word.startsWith(
                                currentQuery,
                                true
                            )
                        } != null
                    } else {
                        true
                    }
                } ?: true

                val filterLanguage = if (!selectedLanguageCode.isNullOrEmpty()) {
                    it.lang == selectedLanguageCode
                } else {
                    true
                }
                return@filter filterEnrolled && filterQuery && filterLanguage
            }
            updateState(filteredClasses)
        }
    }

    private fun updateState(list: List<Classes>) {
        val emptyData = list.isEmpty()
        setState {
            copy(
                searchEnabled = !emptyData,
                itemList = list.toClassesData(),
                showNoContent = emptyData,
            )
        }
    }

    private fun List<Classes>.toClassesData(): List<ClassesItemContainer> {
        return groupBy { it.startTime.toDate() }
            .flatMap {
                val title = "${it.key.toDate().relativeDay(stringProvider)}, ${it.key}"
                mutableListOf<ClassesItemContainer>().apply {
                    add(ClassesItemContainer(ClassesItemTypes.HEADER, ClassesHeader(it.key, title)))
                    addAll(it.value.map { ClassesItemContainer(ClassesItemTypes.CLASS, it) })
                }
            }
    }
}

enum class ClassesItemTypes {
    HEADER,
    CLASS
}

data class ClassesItemContainer(val type: ClassesItemTypes, val data: Any) {
    fun areItemsSame(other: ClassesItemContainer): Boolean {
        if (this.type != other.type) return false
        return when (data) {
            is ClassesHeader -> data.title == (other.data as ClassesHeader).title
            is Classes -> data.id == (other.data as Classes).id
            else -> false
        }
    }
}

data class ClassesHeader(val date: String, val title: String)

data class HomeViewState(
    val isLoading: Boolean = true,
    val showError: Boolean = false,
    val showNoContent: Boolean = false,
    val searchEnabled: Boolean = false,
    val itemList: List<ClassesItemContainer> = arrayListOf()
) : ViewState

sealed class HomeViewActions : ViewModelAction {
    data class Query(val query: String?) : HomeViewActions()
    data class FilterLanguage(val langCode: String?) : HomeViewActions()
    data class FilterEnrolled(val enrolledOnly: Boolean) : HomeViewActions()
}
