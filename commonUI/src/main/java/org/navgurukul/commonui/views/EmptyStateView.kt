package org.navgurukul.commonui.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.empty_state.view.*
import org.navgurukul.commonui.R
class EmptyStateView @JvmOverloads constructor(
    context: Context,
    attributes: AttributeSet? = null,
    defaultStyleAttr: Int = 0,
    defaultStyleRes: Int = 0
) : ConstraintLayout(context, attributes, defaultStyleAttr, defaultStyleRes) {

    enum class State {
        NO_CONTENT, ERROR, LOADING
    }

    var state: State = State.NO_CONTENT
        set(value) {
            field = value
            when (value) {
                State.NO_CONTENT -> {
                    emptyStateDescription.text = context.getString(R.string.empty_state_no_content_description)
                    emptyStateTitle.text = context.getString(R.string.empty_state_no_content_title)
                    emptyStateImage.setImageResource(R.drawable.illus_no_content)
                    progressBar.visibility = View.GONE
                }
                State.ERROR -> {
                    emptyStateDescription.text = context.getString(R.string.empty_state_error_description)
                    emptyStateTitle.text = context.getString(R.string.empty_state_error_title)
                    emptyStateImage.setImageResource(R.drawable.illus_no_internet)
                    progressBar.visibility = View.GONE
                }
                State.LOADING -> {
                    progressBar.visibility = View.VISIBLE
                    emptyStateImage.visibility = View.GONE
                    emptyStateTitle.visibility = View.GONE
                    emptyStateDescription.visibility = View.GONE
                }
            }
        }

    init {
        inflate(context, R.layout.empty_state, this)
        progressBar.visibility = View.GONE
    }
}
