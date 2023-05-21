package org.navgurukul.chat.features.attachmentviewer

sealed class AttachmentEvents {
    data class VideoEvent(val isPlaying: Boolean, val progress: Int, val duration: Int) : AttachmentEvents()
}

interface AttachmentEventListener {
    fun onEvent(event: AttachmentEvents)
}

sealed class AttachmentCommands {
    object PauseVideo : AttachmentCommands()
    object StartVideo : AttachmentCommands()
    data class SeekTo(val percentProgress: Int) : AttachmentCommands()
}
