package org.navgurukul.chat.features.reactions

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.navgurukul.commonui.platform.SingleLiveEvent

class EmojiChooserViewModel : ViewModel() {

    val navigateEvent: SingleLiveEvent<String> = SingleLiveEvent()
    var selectedReaction: String? = null
    var eventId: String? = null

    val currentSection: MutableLiveData<Int> = MutableLiveData()
    val moveToSection: MutableLiveData<Int> = MutableLiveData()

    fun onReactionSelected(reaction: String) {
        selectedReaction = reaction
        navigateEvent.setValue(NAVIGATE_FINISH)
    }

    // Called by the Fragment, when the List is scrolled
    fun setCurrentSection(section: Int) {
        currentSection.value = section
    }

    // Called by the Activity, when a tab item is clicked
    fun scrollToSection(section: Int) {
        moveToSection.value = section
    }

    companion object {
        const val NAVIGATE_FINISH = "NAVIGATE_FINISH"
    }
}
