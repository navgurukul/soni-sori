package org.navgurukul.chat.features.home.room.detail.composer

import android.content.Context
import android.net.Uri
import android.text.Editable
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.text.toSpannable
import androidx.transition.AutoTransition
import androidx.transition.Transition
import androidx.transition.TransitionManager
import kotlinx.android.synthetic.main.merge_composer_layout.view.*
import org.navgurukul.chat.R
import org.navgurukul.commonui.themes.ThemeUtils

/**
 * Encapsulate the timeline composer UX.
 *
 */
class TextComposerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    interface Callback : ComposerEditText.Callback {
        fun onCloseRelatedMessage()
        fun onSendMessage(text: CharSequence)
        fun onAddAttachment()
    }

    val composerRelatedMessageTitle: TextView by lazy {
        findViewById<TextView>(R.id.composer_related_message_sender)
    }

    val composerRelatedMessageContent: TextView by lazy {
        findViewById<TextView>(R.id.composer_related_message_preview)
    }

    val composerRelatedMessageActionIcon: ImageView by lazy {
        findViewById<ImageView>(R.id.composer_related_message_action_image)
    }

    val composerRelatedMessageAvatar: ImageView by lazy {
        findViewById<ImageView>(R.id.composer_related_message_avatar_view)
    }

    var callback: Callback? = null

    private var currentConstraintSetId: Int = -1

    private val animationDuration = 100L

    val composerAvatarImageView: ImageView

    val text: Editable?
        get() = composerEditText.text

    init {
        inflate(context, R.layout.merge_composer_layout, this)
        collapse(false)
        composerEditText.callback = object : ComposerEditText.Callback {
            override fun onRichContentSelected(contentUri: Uri): Boolean {
                return callback?.onRichContentSelected(contentUri) ?: false
            }
        }
        composer_related_message_close.setOnClickListener {
            collapse()
            callback?.onCloseRelatedMessage()
        }

        sendButton.setOnClickListener {
            val textMessage = text?.toSpannable() ?: ""
            callback?.onSendMessage(textMessage)
        }

        attachmentButton.setOnClickListener {
            callback?.onAddAttachment()
        }

        composerAvatarImageView = findViewById(R.id.composer_avatar_view)
        setBackgroundColor(ThemeUtils.getColor(context, R.attr.colorPrimaryVariant))
    }

    fun collapse(animate: Boolean = true, transitionComplete: (() -> Unit)? = null) {
        if (currentConstraintSetId == R.layout.constraint_set_composer_layout_compact) {
            // ignore we good
            return
        }
        currentConstraintSetId = R.layout.constraint_set_composer_layout_compact
        if (animate) {
            val transition = AutoTransition()
            transition.duration = animationDuration
            transition.addListener(object : Transition.TransitionListener {
                override fun onTransitionEnd(transition: Transition) {
                    transitionComplete?.invoke()
                }

                override fun onTransitionResume(transition: Transition) {}

                override fun onTransitionPause(transition: Transition) {}

                override fun onTransitionCancel(transition: Transition) {}

                override fun onTransitionStart(transition: Transition) {}
            }
            )
            TransitionManager.beginDelayedTransition((parent as? ViewGroup ?: this), transition)
        }
        ConstraintSet().also {
            it.clone(context, currentConstraintSetId)
            it.applyTo(this)
        }
    }

    fun expand(animate: Boolean = true, transitionComplete: (() -> Unit)? = null) {
        if (currentConstraintSetId == R.layout.constraint_set_composer_layout_expanded) {
            // ignore we good
            return
        }
        currentConstraintSetId = R.layout.constraint_set_composer_layout_expanded
        if (animate) {
            val transition = AutoTransition()
            transition.duration = animationDuration
            transition.addListener(object : Transition.TransitionListener {
                override fun onTransitionEnd(transition: Transition) {
                    transitionComplete?.invoke()
                }

                override fun onTransitionResume(transition: Transition) {}

                override fun onTransitionPause(transition: Transition) {}

                override fun onTransitionCancel(transition: Transition) {}

                override fun onTransitionStart(transition: Transition) {}
            }
            )
            TransitionManager.beginDelayedTransition((parent as? ViewGroup ?: this), transition)
        }
        ConstraintSet().also {
            it.clone(context, currentConstraintSetId)
            it.applyTo(this)
        }
    }

//    fun setRoomEncrypted(isEncrypted: Boolean, roomEncryptionTrustLevel: RoomEncryptionTrustLevel?) {
//        if (isEncrypted) {
//            composerEditText.setHint(R.string.room_message_placeholder)
//            composerShieldImageView.isVisible = true
//            val shieldRes = when (roomEncryptionTrustLevel) {
//                RoomEncryptionTrustLevel.Trusted -> R.drawable.ic_shield_trusted
//                RoomEncryptionTrustLevel.Warning -> R.drawable.ic_shield_warning
//                else                             -> R.drawable.ic_shield_black
//            }
//            composerShieldImageView.setImageResource(shieldRes)
//        } else {
//            composerEditText.setHint(R.string.room_message_placeholder)
//            composerShieldImageView.isVisible = false
//        }
//    }
}