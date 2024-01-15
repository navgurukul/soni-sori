package org.navgurukul.learn.ui.learn.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.AssessmentType
import org.navgurukul.learn.courses.db.models.OptionResponse
import org.navgurukul.learn.courses.db.models.OptionType
import org.navgurukul.learn.courses.db.models.OptionViewState
import org.navgurukul.learn.databinding.ItemMcqOptionBinding
import org.navgurukul.learn.ui.common.DataBoundListAdapter

class OptionSelectionAdapter(
    val callback: ((List<OptionResponse>) -> Unit)? = null,
    private val assessmentType: AssessmentType? = null
) : DataBoundListAdapter<OptionResponse, ItemMcqOptionBinding>(
    mDiffCallback = object : DiffUtil.ItemCallback<OptionResponse>() {
        override fun areItemsTheSame(oldItem: OptionResponse, newItem: OptionResponse) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: OptionResponse, newItem: OptionResponse) =
            oldItem == newItem
    }
) {
    private val selectedOptions = mutableListOf<OptionResponse>()

    override fun createBinding(parent: ViewGroup, viewType: Int): ItemMcqOptionBinding =
        ItemMcqOptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    override fun bind(
        holder: DataBoundListAdapter.DataBoundViewHolder<ItemMcqOptionBinding>,
        item: OptionResponse
    ) {
        val binding = holder.binding

        binding.apply {
            val isTextOption = item.optionType == OptionType.text

            tvOption.visibility = if (isTextOption) View.VISIBLE else View.GONE
            ivImgOption.visibility = if (isTextOption) View.GONE else View.VISIBLE

            if (isTextOption) {
                tvOption.text = HtmlCompat.fromHtml(item.value, HtmlCompat.FROM_HTML_MODE_COMPACT)
            } else {
                Glide.with(ivImgOption.context)
                    .load(item.value)
                    .centerCrop()
                    .into(ivImgOption)
            }

            when(item.viewState){
                OptionViewState.SELECTED -> {
                        setOptionSelectedUI(binding, isTextOption)
                }
                OptionViewState.NOT_SELECTED -> {
                    setOptionNotSelectedUI(binding, isTextOption)

                }
                OptionViewState.INCORRECT -> {
                    setIncorrectUI(binding, isTextOption)
                }
                OptionViewState.CORRECT -> {
                    setCorrectUI(binding, isTextOption)
                }
                OptionViewState.PARTIALLY_CORRECT -> {
                    setCorrectUI(binding, isTextOption)
                }
                OptionViewState.PARTIALLY_INCORRECT -> {
                    setIncorrectUI(binding, isTextOption)
                }
            }

            root.setOnClickListener {
                when (assessmentType) {
                    AssessmentType.multiple -> {
                        if (selectedOptions.contains(item)) {
                            selectedOptions.remove(item)
                            item.viewState = OptionViewState.NOT_SELECTED
                        } else {
                            selectedOptions.add(item)
                            item.viewState = OptionViewState.SELECTED
                        }
                        callback?.invoke(selectedOptions)
                        bind(holder, item)
                    }
                    AssessmentType.single -> {
                        handleSingleSelection(item)
                    }
                }
            }
        }
    }


    private fun setOptionSelectedUI(binding: ItemMcqOptionBinding, isTextOption: Boolean) {
        binding.apply {
            tvCardOption.setCardBackgroundColor(Color.parseColor("#E9F5E9"))
            tvCardOption.strokeColor = Color.parseColor("#48A145")

            if (isTextOption) {
                tvRadioButtonOption.isChecked = true
                tvRadioButtonOption.buttonTintList = ColorStateList.valueOf(Color.parseColor("#48A145"))
            } else {
                checkBox.isChecked = true
                checkBox.visibility = View.VISIBLE
                checkBox.buttonTintList = ColorStateList.valueOf(Color.parseColor("#48A145"))
                tvRadioButtonOption.visibility = View.GONE
            }
        }
    }

    private fun setOptionNotSelectedUI(binding: ItemMcqOptionBinding, isTextOption: Boolean) {
        binding.apply {
            tvRadioButtonOption.visibility = View.VISIBLE
            checkBox.visibility = View.GONE

            if (isTextOption) {
                tvCardOption.setCardBackgroundColor(Color.parseColor("#ffffff"))
            } else {
                tvCardOption.setCardBackgroundColor(Color.parseColor("#ffffff"))
                tvCardOption.strokeColor = Color.parseColor("#ffffff")
            }
        }
    }

    private fun setIncorrectUI(binding: ItemMcqOptionBinding, isTextOption: Boolean) {
        binding.apply {
            tvCardOption.setCardBackgroundColor(Color.parseColor("#FFE5E3"))
            tvCardOption.strokeColor = Color.parseColor("#F44336")

            if (isTextOption) {
                tvRadioButtonOption.isChecked = true
                tvRadioButtonOption.setButtonDrawable(R.drawable.cancel_circle_optionincorrect)
                tvRadioButtonOption.buttonTintList = ColorStateList.valueOf(Color.parseColor("#D63447"))
            } else {
                checkBox.isChecked = true
                checkBox.setButtonDrawable(R.drawable.cancel_circle_optionincorrect)
                checkBox.buttonTintList = ColorStateList.valueOf(Color.parseColor("#D63447"))
            }
        }
    }

    private fun setCorrectUI(binding: ItemMcqOptionBinding, isTextOption: Boolean) {
        binding.apply {
            tvCardOption.setCardBackgroundColor(Color.parseColor("#E9F5E9"))
            tvCardOption.strokeColor = Color.parseColor("#48A145")

            if (isTextOption) {
                tvRadioButtonOption.isChecked = true
                tvRadioButtonOption.setButtonDrawable(R.drawable.check_circle_correctoption)
                tvRadioButtonOption.buttonTintList = ColorStateList.valueOf(Color.parseColor("#48A145"))
            } else {
                checkBox.isChecked = true
                checkBox.setButtonDrawable(R.drawable.check_circle_correctoption)
                checkBox.buttonTintList = ColorStateList.valueOf(Color.parseColor("#48A145"))
            }
        }
    }


    private fun handleSingleSelection(item: OptionResponse) {
        selectedOptions.clear()
        selectedOptions.add(item)
        callback?.invoke(selectedOptions)
    }
}
