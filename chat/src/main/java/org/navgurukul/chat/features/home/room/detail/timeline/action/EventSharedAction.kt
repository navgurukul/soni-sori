package org.navgurukul.chat.features.home.room.detail.timeline.action

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import org.matrix.android.sdk.api.session.room.model.message.MessageWithAttachmentContent
import org.navgurukul.chat.R
import org.navgurukul.chat.features.home.room.detail.timeline.item.MessageInformationData
import org.navgurukul.commonui.platform.ViewModelAction

sealed class EventSharedAction(@StringRes val titleRes: Int,
                               @DrawableRes val iconResId: Int,
                               val destructive: Boolean = false) : ViewModelAction {
    object Separator :
            EventSharedAction(0, 0)

    data class OpenUserProfile(val userId: String) :
            EventSharedAction(0, 0)

    data class AddReaction(val eventId: String) :
            EventSharedAction(R.string.message_add_reaction, R.drawable.bg_unread_highlight)

    data class Copy(val content: String) :
            EventSharedAction(R.string.message_copy, R.drawable.bg_unread_highlight)

    data class Edit(val eventId: String) :
            EventSharedAction(R.string.edit, R.drawable.bg_unread_highlight)

    data class Quote(val eventId: String) :
            EventSharedAction(R.string.quote, R.drawable.bg_unread_highlight)

    data class Reply(val eventId: String) :
            EventSharedAction(R.string.reply, R.drawable.bg_unread_highlight)

    data class Share(val eventId: String, val messageContent: MessageWithAttachmentContent) :
            EventSharedAction(R.string.share, R.drawable.ic_share)

    data class Save(val eventId: String, val messageContent: MessageWithAttachmentContent) :
            EventSharedAction(R.string.save, R.drawable.bg_unread_highlight)

    data class Resend(val eventId: String) :
            EventSharedAction(R.string.global_retry, R.drawable.bg_unread_highlight)

    data class Remove(val eventId: String) :
            EventSharedAction(R.string.remove, R.drawable.bg_unread_highlight, true)

    data class Redact(val eventId: String, val askForReason: Boolean) :
            EventSharedAction(R.string.message_action_item_redact, R.drawable.bg_unread_highlight, true)

    data class Cancel(val eventId: String) :
            EventSharedAction(R.string.cancel, R.drawable.bg_unread_highlight)

    data class ViewSource(val content: String) :
            EventSharedAction(R.string.view_source, R.drawable.bg_unread_highlight)

    data class ViewDecryptedSource(val content: String) :
            EventSharedAction(R.string.view_decrypted_source, R.drawable.bg_unread_highlight)

//    data class CopyPermalink(val eventId: String) :
//            EventSharedAction(R.string.permalink, R.drawable.ic_permalink)

    data class ReportContent(val eventId: String, val senderId: String?) :
            EventSharedAction(R.string.report_content, R.drawable.bg_unread_highlight)

    data class ReportContentSpam(val eventId: String, val senderId: String?) :
            EventSharedAction(R.string.report_content_spam, R.drawable.bg_unread_highlight)

    data class ReportContentInappropriate(val eventId: String, val senderId: String?) :
            EventSharedAction(R.string.report_content_inappropriate, R.drawable.bg_unread_highlight)

    data class ReportContentCustom(val eventId: String, val senderId: String?) :
            EventSharedAction(R.string.report_content_custom, R.drawable.bg_unread_highlight)

    data class IgnoreUser(val senderId: String?) :
            EventSharedAction(R.string.message_ignore_user, R.drawable.bg_unread_highlight, true)

    data class QuickReact(val eventId: String, val clickedOn: String, val add: Boolean) :
            EventSharedAction(0, 0)

    data class ViewReactions(val messageInformationData: MessageInformationData) :
            EventSharedAction(R.string.message_view_reaction, R.drawable.bg_unread_highlight)

    data class ViewEditHistory(val messageInformationData: MessageInformationData) :
            EventSharedAction(R.string.message_view_edit_history, R.drawable.bg_unread_highlight)

    // An url in the event preview has been clicked
    data class OnUrlClicked(val url: String, val title: String) :
            EventSharedAction(0, 0)

    // An url in the event preview has been long clicked
    data class OnUrlLongClicked(val url: String) :
            EventSharedAction(0, 0)

    data class ReRequestKey(val eventId: String) :
            EventSharedAction(R.string.e2e_re_request_encryption_key, R.drawable.bg_unread_highlight)

    object UseKeyBackup :
            EventSharedAction(R.string.e2e_use_keybackup, R.drawable.bg_unread_highlight)
}
