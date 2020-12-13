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
            EventSharedAction(R.string.message_add_reaction, R.drawable.ic_add_reaction)

    data class Copy(val content: String) :
            EventSharedAction(R.string.message_copy, R.drawable.ic_copy)

    data class Edit(val eventId: String) :
            EventSharedAction(R.string.edit, R.drawable.ic_edit)

    data class Quote(val eventId: String) :
            EventSharedAction(R.string.quote, R.drawable.ic_quote)

    data class Reply(val eventId: String) :
            EventSharedAction(R.string.reply, R.drawable.ic_reply)

    data class Share(val eventId: String, val messageContent: MessageWithAttachmentContent) :
            EventSharedAction(R.string.share, R.drawable.ic_share)

    data class Save(val eventId: String, val messageContent: MessageWithAttachmentContent) :
            EventSharedAction(R.string.save, R.drawable.ic_material_save)

    data class Resend(val eventId: String) :
            EventSharedAction(R.string.global_retry, R.drawable.ic_refresh_cw)

    data class Remove(val eventId: String) :
            EventSharedAction(R.string.remove, R.drawable.ic_trash, true)

    data class Redact(val eventId: String, val askForReason: Boolean) :
            EventSharedAction(R.string.message_action_item_redact, R.drawable.ic_delete, true)

    data class Cancel(val eventId: String) :
            EventSharedAction(R.string.cancel, R.drawable.ic_close_round)

    data class ViewSource(val content: String) :
            EventSharedAction(R.string.view_source, R.drawable.ic_view_source)

    data class ViewDecryptedSource(val content: String) :
            EventSharedAction(R.string.view_decrypted_source, R.drawable.ic_view_source)

//    data class CopyPermalink(val eventId: String) :
//            EventSharedAction(R.string.permalink, R.drawable.ic_permalink)

    data class ReportContent(val eventId: String, val senderId: String?) :
            EventSharedAction(R.string.report_content, R.drawable.ic_flag)

    data class ReportContentSpam(val eventId: String, val senderId: String?) :
            EventSharedAction(R.string.report_content_spam, R.drawable.ic_flag)

    data class ReportContentInappropriate(val eventId: String, val senderId: String?) :
            EventSharedAction(R.string.report_content_inappropriate, R.drawable.ic_report_inappropriate)

    data class ReportContentCustom(val eventId: String, val senderId: String?) :
            EventSharedAction(R.string.report_content_custom, R.drawable.ic_report_custom)

    data class IgnoreUser(val senderId: String?) :
            EventSharedAction(R.string.message_ignore_user, R.drawable.ic_alert_triangle, true)

    data class QuickReact(val eventId: String, val clickedOn: String, val add: Boolean) :
            EventSharedAction(0, 0)

    data class ViewReactions(val messageInformationData: MessageInformationData) :
            EventSharedAction(R.string.message_view_reaction, R.drawable.ic_view_reactions)

    data class ViewEditHistory(val messageInformationData: MessageInformationData) :
            EventSharedAction(R.string.message_view_edit_history, R.drawable.ic_view_edit_history)

    // An url in the event preview has been clicked
    data class OnUrlClicked(val url: String, val title: String) :
            EventSharedAction(0, 0)

    // An url in the event preview has been long clicked
    data class OnUrlLongClicked(val url: String) :
            EventSharedAction(0, 0)

    data class ReRequestKey(val eventId: String) :
            EventSharedAction(R.string.e2e_re_request_encryption_key, R.drawable.ic_key)

    object UseKeyBackup :
            EventSharedAction(R.string.e2e_use_keybackup, R.drawable.ic_shield_black)
}
