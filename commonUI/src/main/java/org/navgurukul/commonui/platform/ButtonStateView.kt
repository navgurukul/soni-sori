
package org.navgurukul.commonui.platform

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.view_button_state.view.*
import org.navgurukul.commonui.R

class ButtonStateView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : FrameLayout(context, attrs, defStyle) {

    sealed class State {
        object Button : State()
        object Loading : State()
        object Loaded : State()
        object Error : State()
    }

    var callback: Callback? = null

    interface Callback {
        fun onButtonClicked()
        fun onRetryClicked()
    }

    // Big or Flat button
    var button: Button

    init {
        View.inflate(context, R.layout.view_button_state, this)
        layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        buttonStateRetry.setOnClickListener {
            callback?.onRetryClicked()
        }

        // Read attributes
        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.ButtonStateView,
                0, 0)
                .apply {
                    try {
                        if (getBoolean(R.styleable.ButtonStateView_bsv_use_flat_button, true)) {
                            button = buttonStateButtonFlat
                            buttonStateButtonBig.isVisible = false
                        } else {
                            button = buttonStateButtonBig
                            buttonStateButtonFlat.isVisible = false
                        }

                        button.text = getString(R.styleable.ButtonStateView_bsv_button_text)
                        buttonStateLoaded.setImageDrawable(getDrawable(R.styleable.ButtonStateView_bsv_loaded_image_src))
                    } finally {
                        recycle()
                    }
                }

        button.setOnClickListener {
            callback?.onButtonClicked()
        }
    }

    fun render(newState: State) {
        if (newState == State.Button) {
            button.isVisible = true
        } else {
            // We use isInvisible because we want to keep button space in the layout
            button.isInvisible = true
        }

        buttonStateLoading.isVisible = newState == State.Loading
        buttonStateLoaded.isVisible = newState == State.Loaded
        buttonStateRetry.isVisible = newState == State.Error
    }
}