package org.navgurukul.commonui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
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
        NO_CONTENT, ERROR, LOADING, OFFLINE
    }

    var state: State = State.NO_CONTENT
        set(value) {
            field = value
            when (value) {
                State.NO_CONTENT -> {
                    binding.emptyStateDescription.text =
                        context.getString(R.string.empty_state_no_content_description)
                    binding.emptyStateTitle.text =
                        context.getString(R.string.empty_state_no_content_title)
                    binding.emptyStateImage.setImageResource(R.drawable.illus_no_content)
                    binding.progressBar.visibility = View.GONE
                }
                State.ERROR -> {
                    binding.emptyStateDescription.text =
                        context.getString(R.string.empty_state_error_description)
                    binding.emptyStateTitle.text = context.getString(R.string.empty_state_error_title)
                    binding.emptyStateImage.setImageResource(R.drawable.illus_no_internet)
                    binding.progressBar.visibility = View.GONE
                }
                State.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.emptyStateImage.visibility = View.GONE
                    binding.emptyStateTitle.visibility = View.GONE
                    binding.emptyStateDescription.visibility = View.GONE
                }
                else -> {}
            }
        }

    private val binding: EmptyStateBinding =
        EmptyStateBinding.inflate(LayoutInflater.from(context), this)

    init {
        binding.progressBar.visibility = View.GONE
    }
}
