package org.navgurukul.commonui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import org.navgurukul.commonui.R
import org.navgurukul.commonui.databinding.EmptyStateBinding

class EmptyStateView @JvmOverloads constructor(
    context: Context,
    attributes: AttributeSet? = null,
    defaultStyleAttr: Int = 0,
    defaultStyleRes: Int = 0
) : ConstraintLayout(context, attributes, defaultStyleAttr, defaultStyleRes) {

    enum class State {
        NO_CONTENT, ERROR
    }

    lateinit var binding : EmptyStateBinding
    var state: State = State.NO_CONTENT
        set(value) {
            field = value
            when (value) {
                State.NO_CONTENT -> {
                    binding.apply {
                        emptyStateDescription.text = context.getString(R.string.empty_state_no_content_description)
                        emptyStateTitle.text = context.getString(R.string.empty_state_no_content_title)
                        emptyStateImage.setImageResource(R.drawable.illus_no_content)
                    }

                }
                State.ERROR -> {
                    binding.apply {
                        emptyStateDescription.text = context.getString(R.string.empty_state_error_description)
                        emptyStateTitle.text = context.getString(R.string.empty_state_error_title)
                        emptyStateImage.setImageResource(R.drawable.illus_no_internet)
                    }
                }
            }
        }

    init {
        binding = EmptyStateBinding.inflate(LayoutInflater.from(this.context), this)
        View.inflate(context, R.layout.empty_state, this)
        layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

    }
}