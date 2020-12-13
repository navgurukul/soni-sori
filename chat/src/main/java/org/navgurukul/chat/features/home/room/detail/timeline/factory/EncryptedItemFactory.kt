
package org.navgurukul.chat.features.home.room.detail.timeline.factory

import org.matrix.android.sdk.api.session.crypto.MXCryptoError
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.internal.crypto.model.event.EncryptedEventContent
import me.gujun.android.span.image
import me.gujun.android.span.span
import org.navgurukul.chat.R
import org.navgurukul.chat.core.epoxy.MerakiEpoxyModel
import org.navgurukul.chat.core.resources.ColorProvider
import org.navgurukul.chat.core.resources.DrawableProvider
import org.navgurukul.chat.features.home.room.detail.timeline.TimelineEventController
import org.navgurukul.chat.features.home.room.detail.timeline.helper.AvatarSizeProvider
import org.navgurukul.chat.features.home.room.detail.timeline.helper.MessageInformationDataFactory
import org.navgurukul.chat.features.home.room.detail.timeline.helper.MessageItemAttributesFactory
import org.navgurukul.chat.features.home.room.detail.timeline.item.MessageTextItem_
import org.navgurukul.chat.features.home.room.detail.timeline.tools.createLinkMovementMethod
import org.navgurukul.chat.features.settings.ChatPreferences
import org.navgurukul.commonui.resources.StringProvider

// This class handles timeline events who haven't been successfully decrypted
class EncryptedItemFactory(
    private val messageInformationDataFactory: MessageInformationDataFactory,
    private val colorProvider: ColorProvider,
    private val stringProvider: StringProvider,
    private val avatarSizeProvider: AvatarSizeProvider,
    private val drawableProvider: DrawableProvider,
    private val attributesFactory: MessageItemAttributesFactory,
    private val chatPreferences: ChatPreferences
) {

    fun create(event: TimelineEvent,
               nextEvent: TimelineEvent?,
               highlight: Boolean,
               callback: TimelineEventController.Callback?): MerakiEpoxyModel<*>? {
        event.root.eventId ?: return null

        return when {
            EventType.ENCRYPTED == event.root.getClearType() -> {
                val cryptoError = event.root.mCryptoError

                val spannableStr = if (chatPreferences.developerMode()) {
                    val errorDescription =
                            if (cryptoError == MXCryptoError.ErrorType.UNKNOWN_INBOUND_SESSION_ID) {
                                stringProvider.getString(R.string.notice_crypto_error_unkwown_inbound_session_id)
                            } else {
                                // TODO i18n
                                cryptoError?.name
                            }

                    val message = stringProvider.getString(R.string.encrypted_message).takeIf { cryptoError == null }
                            ?: stringProvider.getString(R.string.notice_crypto_unable_to_decrypt, errorDescription)
                    span(message) {
                        textStyle = "italic"
                        textColor = colorProvider.getColorFromAttribute(R.attr.textSecondary)
                    }
                } else {
                    val colorFromAttribute = colorProvider.getColorFromAttribute(R.attr.textSecondary)
                    if (cryptoError == null) {
                        span(stringProvider.getString(R.string.encrypted_message)) {
                            textStyle = "italic"
                            textColor = colorFromAttribute
                        }
                    } else {
                        when (cryptoError) {
                            MXCryptoError.ErrorType.KEYS_WITHHELD -> {
                                span {
                                    apply {
                                        drawableProvider.getDrawable(R.drawable.ic_forbidden, colorFromAttribute)?.let {
                                            image(it, "baseline")
                                        }
                                    }
                                    span(stringProvider.getString(R.string.notice_crypto_unable_to_decrypt_final)) {
                                        textStyle = "italic"
                                        textColor = colorFromAttribute
                                    }
                                }
                            }
                            else                                  -> {
                                span {
                                    apply {
                                        drawableProvider.getDrawable(R.drawable.ic_clock, colorFromAttribute)?.let {
                                            image(it, "baseline")
                                        }
                                    }
                                    span(stringProvider.getString(R.string.notice_crypto_unable_to_decrypt_friendly)) {
                                        textStyle = "italic"
                                        textColor = colorFromAttribute
                                    }
                                }
                            }
                        }
                    }
                }

                val informationData = messageInformationDataFactory.create(event, nextEvent)
                val attributes = attributesFactory.create(event.root.content.toModel<EncryptedEventContent>(), informationData, callback)
                return MessageTextItem_()
                        .highlighted(highlight)
                        .attributes(attributes)
                        .message(spannableStr)
                        .movementMethod(createLinkMovementMethod(callback))
            }
            else                                             -> null
        }
    }
}
