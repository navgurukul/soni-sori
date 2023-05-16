package org.navgurukul.commonui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import org.navgurukul.commonui.R
import org.navgurukul.commonui.databinding.ViewJumpToReadMarkerBinding

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
    lateinit var binding : ViewJumpToReadMarkerBinding

    init {
        setupView()
    }

    private fun setupView() {
        binding = ViewJumpToReadMarkerBinding.inflate(LayoutInflater.from(this.context), this)
        View.inflate(context, R.layout.view_jump_to_read_marker, this)
        setBackgroundColor(ContextCompat.getColor(context, R.color.notification_accent_color))
        binding.jumpToReadMarkerLabelView.setOnClickListener {
            callback?.onJumpToReadMarkerClicked()
        }
        binding.closeJumpToReadMarkerView.setOnClickListener {
            visibility = View.INVISIBLE
            callback?.onClearReadMarkerClicked()
        }
    }
}
