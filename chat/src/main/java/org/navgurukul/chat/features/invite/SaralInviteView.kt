package org.navgurukul.chat.features.invite

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import org.matrix.android.sdk.api.session.room.members.ChangeMembershipState
import org.matrix.android.sdk.api.session.user.model.User
import org.matrix.android.sdk.api.util.toMatrixItem
import kotlinx.android.synthetic.main.saral_invite_view.view.*
import org.koin.java.KoinJavaComponent.inject
import org.navgurukul.chat.R
import org.navgurukul.chat.features.home.AvatarRenderer
import org.navgurukul.commonui.platform.ButtonStateView

class SaralInviteView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : ConstraintLayout(context, attrs, defStyle) {

    interface Callback {
        fun onAcceptInvite()
        fun onRejectInvite()
    }

    enum class Mode {
        LARGE,
        SMALL
    }

    private val avatarRenderer: AvatarRenderer by inject(AvatarRenderer::class.java)
    var callback: Callback? = null

    init {
        View.inflate(context, R.layout.saral_invite_view, this)
        inviteAcceptView.callback = object : ButtonStateView.Callback {
            override fun onButtonClicked() {
                callback?.onAcceptInvite()
            }

            override fun onRetryClicked() {
                callback?.onAcceptInvite()
            }
        }

        inviteRejectView.callback = object : ButtonStateView.Callback {
            override fun onButtonClicked() {
                callback?.onRejectInvite()
            }

            override fun onRetryClicked() {
                callback?.onRejectInvite()
            }
        }
    }

    fun render(sender: User, mode: Mode = Mode.LARGE, changeMembershipState: ChangeMembershipState) {
        if (mode == Mode.LARGE) {
            updateLayoutParams { height = LayoutParams.MATCH_CONSTRAINT }
            avatarRenderer.render(sender.toMatrixItem(), inviteAvatarView)
            inviteIdentifierView.text = sender.userId
            inviteNameView.text = sender.displayName
            inviteLabelView.text = context.getString(R.string.send_you_invite)
        } else {
            updateLayoutParams { height = LayoutParams.WRAP_CONTENT }
            inviteAvatarView.visibility = View.GONE
            inviteIdentifierView.visibility = View.GONE
            inviteNameView.visibility = View.GONE
            inviteLabelView.text = context.getString(R.string.invited_by, sender.userId)
        }
        InviteButtonStateBinder.bind(inviteAcceptView, inviteRejectView, changeMembershipState)
    }
}
