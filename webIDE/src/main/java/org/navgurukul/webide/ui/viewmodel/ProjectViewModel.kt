package org.navgurukul.webide.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.navgurukul.webide.extensions.notify

class ProjectViewModel : ViewModel() {

    val openFiles: MutableLiveData<ArrayList<String>> by lazy {
        MutableLiveData<ArrayList<String>>()
    }

    fun addOpenFile(file: String) {
        openFiles.value?.add(file)
        openFiles.notify()
    }

    fun removeOpenFile(file: String) {
        openFiles.value?.remove(file)
        openFiles.notify()
    }
}