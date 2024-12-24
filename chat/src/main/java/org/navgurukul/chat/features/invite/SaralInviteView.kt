package org.navgurukul.chat.features.invite

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import org.matrix.android.sdk.api.session.room.members.ChangeMembershipState
import org.matrix.android.sdk.api.session.user.model.User
import org.matrix.android.sdk.api.util.toMatrixItem
//import org.koin.java.KoinJavaComponent.inject
import org.navgurukul.chat.R
import org.navgurukul.chat.databinding.SaralInviteViewBinding
import org.navgurukul.chat.features.home.AvatarRenderer
import org.navgurukul.commonui.platform.ButtonStateView
import org.koin.java.KoinJavaComponent.inject

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

    private lateinit var binding: SaralInviteViewBinding

    init {
        initView()
    }

    private fun initView() {
        binding = SaralInviteViewBinding.inflate(LayoutInflater.from(context), this)
        binding.inviteAcceptView.callback = object : ButtonStateView.Callback {
            override fun onButtonClicked() {
                callback?.onAcceptInvite()
            }

            override fun onRetryClicked() {
                callback?.onAcceptInvite()
            }
        }

        binding.inviteRejectView.callback = object : ButtonStateView.Callback {
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
            avatarRenderer.render(sender.toMatrixItem(), binding.inviteAvatarView)
            binding.inviteIdentifierView.text = sender.userId
            binding.inviteNameView.text = sender.displayName
            binding.inviteLabelView.text = context.getString(R.string.send_you_invite)
        } else {
            updateLayoutParams { height = LayoutParams.WRAP_CONTENT }
            binding.inviteAvatarView.visibility = View.GONE
            binding.inviteIdentifierView.visibility = View.GONE
            binding.inviteNameView.visibility = View.GONE
            binding.inviteLabelView.text = context.getString(R.string.invited_by, sender.userId)
        }
        InviteButtonStateBinder.bind(binding.inviteAcceptView, binding.inviteRejectView, changeMembershipState)
    }
}
