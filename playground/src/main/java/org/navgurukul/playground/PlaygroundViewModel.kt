package org.navgurukul.playground

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlaygroundViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Playground Fragment"
    }
    val text: LiveData<String> = _text
}