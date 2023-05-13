package org.navgurukul.commonui.platform

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import org.navgurukul.commonui.R
import org.navgurukul.commonui.databinding.ViewButtonStateBinding
import org.navgurukul.commonui.databinding.ViewStateBinding

class StateView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : FrameLayout(context, attrs, defStyle) {

    sealed class State {
        object Content : State()
        object Loading : State()
        data class Empty(val title: CharSequence? = null, val image: Drawable? = null, val message: CharSequence? = null) : State()

        data class Error(val message: CharSequence? = null) : State()
    }

    var eventCallback: EventCallback? = null

    var contentView: View? = null

    var state: State = State.Empty()
        set(newState) {
            if (newState != state) {
                update(newState)
            }
        }

    interface EventCallback {
        fun onRetryClicked()
    }
    lateinit var binding: ViewStateBinding

    init {
        binding = ViewStateBinding.inflate(LayoutInflater.from(this.context), this)

        View.inflate(context, R.layout.view_state, this)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        binding.errorRetryView.setOnClickListener {
            eventCallback?.onRetryClicked()
        }
        state = State.Content
    }

    private fun update(newState: State) {
        binding.apply {
            progressBar.isVisible = newState is State.Loading
            errorView.isVisible = newState is State.Error
            emptyView.isVisible = newState is State.Empty
            contentView?.isVisible = newState is State.Content

            when (newState) {
                is State.Content -> Unit
                is State.Loading -> Unit
                is State.Empty -> {
                    emptyImageView.setImageDrawable(newState.image)
                    emptyMessageView.text = newState.message
                    emptyTitleView.text = newState.title
                }

                is State.Error -> {
                    errorMessageView.text = newState.message
                }
            }
        }
    }
}