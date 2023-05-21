package org.navgurukul.chat.features.home.room.detail

import android.net.Uri
import androidx.annotation.StringRes
import org.matrix.android.sdk.internal.crypto.model.event.WithHeldCode
import org.navgurukul.chat.features.command.Command
import org.navgurukul.commonui.platform.ViewEvents
import java.io.File

/**
 * Transient events for RoomDetail
 */
sealed class RoomDetailFragmentViewEvents : ViewEvents {
    data class Failure(val throwable: Throwable) : RoomDetailFragmentViewEvents()
    data class OnNewTimelineEvents(val eventIds: List<String>) : RoomDetailFragmentViewEvents()

    data class ActionSuccess(val action: RoomDetailAction) : RoomDetailFragmentViewEvents()
    data class ActionFailure(val action: RoomDetailAction, val throwable: Throwable) : RoomDetailFragmentViewEvents()

    data class ShowMessage(val message: String) : RoomDetailFragmentViewEvents()
    data class ShowE2EErrorMessage(val withHeldCode: WithHeldCode?) : RoomDetailFragmentViewEvents()

    data class NavigateToEvent(val eventId: String) : RoomDetailFragmentViewEvents()
    data class OpenDeepLink(val deepLink: String) : RoomDetailFragmentViewEvents()

    abstract class SendMessageResult : RoomDetailFragmentViewEvents()

//    object DisplayPromptForIntegrationManager: RoomDetailFragmentViewEvents()

//    object DisplayEnableIntegrationsWarning: RoomDetailFragmentViewEvents()

    object MessageSent : SendMessageResult()
    data class JoinRoomCommandSuccess(val roomId: String) : SendMessageResult()
    class SlashCommandError(val command: Command) : SendMessageResult()
    class SlashCommandUnknown(val command: String) : SendMessageResult()
    data class SlashCommandHandled(@StringRes val messageRes: Int? = null) : SendMessageResult()
    object SlashCommandResultOk : SendMessageResult()
    class SlashCommandResultError(val throwable: Throwable) : SendMessageResult()
    // TODO Remove
    object SlashCommandNotImplemented : SendMessageResult()

    data class DownloadFileState(
        val mimeType: String?,
        val file: File?,
        val throwable: Throwable?
    ) : RoomDetailFragmentViewEvents()

    data class OpenFile(
        val mimeType: String?,
        val uri: Uri?,
        val throwable: Throwable?
    ) : RoomDetailFragmentViewEvents()

}
