package org.navgurukul.commonui.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.view_jump_to_read_marker.view.*
import org.navgurukul.commonui.R

class JumpToReadMarkerView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    interface Callback {
        fun onJumpToReadMarkerClicked()
        fun onClearReadMarkerClicked()
    }

    var callback: Callback? = null

    init {
        setupView()
    }

    private fun setupView() {
        inflate(context, R.layout.view_jump_to_read_marker, this)
        setBackgroundColor(ContextCompat.getColor(context, R.color.notification_accent_color))
        jumpToReadMarkerLabelView.setOnClickListener {
            callback?.onJumpToReadMarkerClicked()
        }
        closeJumpToReadMarkerView.setOnClickListener {
            visibility = View.INVISIBLE
            callback?.onClearReadMarkerClicked()
        }
    }
}
