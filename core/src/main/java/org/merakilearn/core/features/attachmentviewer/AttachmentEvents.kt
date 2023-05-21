package org.merakilearn.core.features.attachmentviewer

sealed class AttachmentEvents {
    data class VideoEvent(val isPlaying: Boolean, val progress: Int, val duration: Int) : org.merakilearn.core.features.attachmentviewer.AttachmentEvents()
}

interface AttachmentEventListener {
    fun onEvent(event: org.merakilearn.core.features.attachmentviewer.AttachmentEvents)
}

sealed class AttachmentCommands {
    object PauseVideo : org.merakilearn.core.features.attachmentviewer.AttachmentCommands()
    object StartVideo : org.merakilearn.core.features.attachmentviewer.AttachmentCommands()
    data class SeekTo(val percentProgress: Int) : org.merakilearn.core.features.attachmentviewer.AttachmentCommands()
}
