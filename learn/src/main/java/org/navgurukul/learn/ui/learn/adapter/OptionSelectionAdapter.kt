package org.navgurukul.learn.ui.learn.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_mcq_option.view.clOption
import org.navgurukul.learn.R.*
import org.navgurukul.learn.courses.db.models.*
import org.navgurukul.learn.databinding.ItemMcqOptionBinding
import org.navgurukul.learn.ui.common.DataBoundListAdapter

class OptionSelectionAdapter(
    val callback: ((List<OptionResponse>) -> Unit)? = null,
    private val assessmentType: AssessmentType? = null
) :
    DataBoundListAdapter<OptionResponse, ItemMcqOptionBinding>(
        mDiffCallback = object : DiffUtil.ItemCallback<OptionResponse>() {

            override fun areItemsTheSame(
                oldItem: OptionResponse,
                newItem: OptionResponse,
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: OptionResponse,
                newItem: OptionResponse,
            ): Boolean {
                return oldItem == newItem
            }
        }) {
    override fun createBinding(parent: ViewGroup, viewType: Int): ItemMcqOptionBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            layout.item_mcq_option, parent, false
        )
    }

    private val selectedOptions = mutableListOf<OptionResponse>()

    override fun bind(
        holder: DataBoundListAdapter.DataBoundViewHolder<ItemMcqOptionBinding>,
        item: OptionResponse
    ) {
        val binding = holder.binding
        binding.apply {
            if (item.optionType == OptionType.text) {
                tvOption.visibility = View.VISIBLE
                ivImgOption.visibility = View.GONE
                tvOption.text = item.value
                tvOption.text = HtmlCompat.fromHtml(item.value, HtmlCompat.FROM_HTML_MODE_COMPACT)
            } else {
                tvOption.visibility = View.GONE
                ivImgOption.visibility = View.VISIBLE
                Glide
                    .with(ivImgOption.context)
                    .load(item.value)
                    .centerCrop()
                    .into(ivImgOption)
            }
            //if(assessmentType==AssessmentType.multiple){
            //            item.viewState = if (selectedOptions.contains(item)) OptionViewState.SELECTED else OptionViewState.NOT_SELECTED
            //}

            fun setOptionVisibility(optionType: OptionType) {
                if (optionType == OptionType.text) {
                    tvOption.visibility = View.VISIBLE
                    ivImgOption.visibility = View.GONE
                } else {
                    tvOption.visibility = View.GONE
                    ivImgOption.visibility = View.VISIBLE
                }
            }
            when (item.viewState) {
                OptionViewState.SELECTED -> {
                    when (assessmentType) {
                        AssessmentType.single -> {
                            tvCardOption.setCardBackgroundColor(Color.parseColor("#E9F5E9"))
                            tvCardOption.strokeColor = Color.parseColor("#48A145")
                            tvRadioButtonOption.isChecked = true
                            tvRadioButtonOption.buttonTintList =
                                ColorStateList.valueOf(Color.parseColor("#48A145"))

                            setOptionVisibility(item.optionType)
//                            if (item.optionType == OptionType.text) {
//                                ivImgOption.visibility = View.GONE
//                                tvOption.visibility = View.VISIBLE
//                            } else {
//                                ivImgOption.visibility = View.VISIBLE
//                                tvOption.visibility = View.GONE
//                            }
                        }

                        AssessmentType.multiple -> {
                            tvCardOption.setCardBackgroundColor(Color.parseColor("#E9F5E9"))
                            tvCardOption.strokeColor = Color.parseColor("#48A145")
                            checkBox.isChecked = true
                            checkBox.visibility = View.VISIBLE
                            tvRadioButtonOption.visibility = View.GONE
                            checkBox.buttonTintList =
                                ColorStateList.valueOf(Color.parseColor("#48A145"))
                            setOptionVisibility(item.optionType)
//                            if (item.optionType == OptionType.text) {
//                                ivImgOption.visibility = View.GONE
//                                tvOption.visibility = View.VISIBLE
//                            } else {
//                                ivImgOption.visibility = View.VISIBLE
//                                tvOption.visibility = View.GONE
//                            }

                        }
                    }
                }

                OptionViewState.NOT_SELECTED -> {
                    when (assessmentType) {
                        AssessmentType.single -> {
                            checkBox.visibility = View.GONE
                            tvCardOption.setCardBackgroundColor(Color.parseColor("#ffffff"))
                            tvRadioButtonOption.visibility = View.VISIBLE
                            setOptionVisibility(item.optionType)
//                            if (item.optionType == OptionType.text) {
//                                ivImgOption.visibility = View.GONE
//                                tvOption.visibility = View.VISIBLE
//
//                            } else {
//                                ivImgOption.visibility = View.VISIBLE
//                                tvOption.visibility = View.GONE
//                            }
                        }

                        AssessmentType.multiple -> {
                            tvRadioButtonOption.visibility = View.GONE
                            tvCardOption.setCardBackgroundColor(Color.parseColor("#ffffff"))
                            tvCardOption.strokeColor = Color.parseColor("#ffffff")
                            checkBox.visibility = View.VISIBLE
                            checkBox.isChecked = false
                            checkBox.buttonTintList =
                                ColorStateList.valueOf(Color.parseColor("#000000"))
                            setOptionVisibility(item.optionType)
//                            if (item.optionType == OptionType.text) {
//                                ivImgOption.visibility = View.GONE
//                                tvOption.visibility = View.VISIBLE
//                            } else {
//                                ivImgOption.visibility = View.VISIBLE
//                                tvOption.visibility = View.GONE
//                            }
                        }
                    }

                }

                OptionViewState.INCORRECT -> {
                    when (assessmentType) {
                        AssessmentType.single -> {
                            tvCardOption.setCardBackgroundColor(Color.parseColor("#FFE5E3"))
                            tvCardOption.strokeColor = Color.parseColor("#F44336")
                            tvRadioButtonOption.isChecked = true
                            tvRadioButtonOption.setButtonDrawable(drawable.cancel_circle_optionincorrect)
                            tvRadioButtonOption.buttonTintList =
                                ColorStateList.valueOf(Color.parseColor("#D63447"))
                            setOptionVisibility(item.optionType)
//                            if (item.optionType == OptionType.text) {
//                                ivImgOption.visibility = View.GONE
//                                tvOption.visibility = View.VISIBLE
//                            } else {
//                                ivImgOption.visibility = View.VISIBLE
//                                tvOption.visibility = View.GONE
//                            }
                        }

                        AssessmentType.multiple -> {

                            tvRadioButtonOption.visibility = View.GONE
                            checkBox.visibility = View.VISIBLE
                            tvCardOption.setCardBackgroundColor(Color.parseColor("#FFE5E3"))
                            tvCardOption.strokeColor = Color.parseColor("#F44336")
                            checkBox.isChecked = true
                            checkBox.setButtonDrawable(drawable.cancel_circle_optionincorrect)
                            checkBox.buttonTintList =
                                ColorStateList.valueOf(Color.parseColor("#D63447"))
                            setOptionVisibility(item.optionType)
//                            if (item.optionType == OptionType.text) {
//                                ivImgOption.visibility = View.GONE
//                                tvOption.visibility = View.VISIBLE
//
//                            } else {
//                                ivImgOption.visibility = View.VISIBLE
//                                tvOption.visibility = View.GONE
//
//                            }
                        }
                    }
                }

                OptionViewState.CORRECT -> {
                    when (assessmentType) {
                        AssessmentType.single -> {
                            tvCardOption.setCardBackgroundColor(Color.parseColor("#E9F5E9"))
                            tvCardOption.strokeColor = Color.parseColor("#48A145")
                            tvRadioButtonOption.isChecked = true
                            tvRadioButtonOption.setButtonDrawable(drawable.check_circle_correctoption)
                            tvRadioButtonOption.buttonTintList =
                                ColorStateList.valueOf(Color.parseColor("#48A145"))
                            setOptionVisibility(item.optionType)
//                            if (item.optionType == OptionType.text) {
//                                ivImgOption.visibility = View.GONE
//                                tvOption.visibility = View.VISIBLE
//                            } else {
//                                ivImgOption.visibility = View.VISIBLE
//                                tvOption.visibility = View.GONE
//                            }
                        }

                        AssessmentType.multiple -> {
                            tvRadioButtonOption.visibility = View.GONE
                            tvCardOption.setCardBackgroundColor(Color.parseColor("#E9F5E9"))
                            tvCardOption.strokeColor = Color.parseColor("#48A145")
                            checkBox.visibility = View.VISIBLE
                            checkBox.isChecked = true
                            checkBox.setButtonDrawable(drawable.check_circle_correctoption)
                            checkBox.buttonTintList =
                                ColorStateList.valueOf(Color.parseColor("#48A145"))
                            setOptionVisibility(item.optionType)
//                            if (item.optionType == OptionType.text) {
//                                ivImgOption.visibility = View.GONE
//                                tvOption.visibility = View.VISIBLE
//                            } else {
//                                ivImgOption.visibility = View.VISIBLE
//                                tvOption.visibility = View.GONE
//                            }
                        }
                    }
                }

                OptionViewState.PARTIALLY_CORRECT -> {
                    when (assessmentType) {
                        AssessmentType.multiple -> {
                            if (item.optionType == OptionType.text) {
                                ivImgOption.visibility = View.GONE
                                tvOption.visibility = View.VISIBLE
                                tvRadioButtonOption.visibility = View.GONE
                                checkBox.visibility = View.VISIBLE
                                if (item.attemptCount < 2) {
                                    checkBox.isChecked = false
                                    tvCardOption.setCardBackgroundColor(Color.parseColor("#F1F1F1"))
                                    tvCardOption.strokeColor = Color.parseColor("#F1F1F1")
                                    checkBox.buttonTintList =
                                        ColorStateList.valueOf(Color.parseColor("#000000"))

                                } else {
                                    tvCardOption.setCardBackgroundColor(Color.parseColor("#E9F5E9"))
                                    tvCardOption.strokeColor = Color.parseColor("#48A145")
                                    checkBox.isChecked = true
                                    checkBox.setButtonDrawable(drawable.check_circle_correctoption)
                                    checkBox.buttonTintList =
                                        ColorStateList.valueOf(Color.parseColor("#48A145"))
                                }
                            } else {
                                ivImgOption.visibility = View.VISIBLE
                                tvOption.visibility = View.GONE
                                checkBox.visibility = View.VISIBLE
                                tvRadioButtonOption.visibility = View.GONE
                                if (item.attemptCount < 2) {
                                    tvCardOption.setCardBackgroundColor(Color.parseColor("#F1F1F1"))
                                    tvCardOption.strokeColor = Color.parseColor("#F1F1F1")
                                    checkBox.isChecked = false
                                    checkBox.buttonTintList =
                                        ColorStateList.valueOf(Color.parseColor("#000000"))
                                } else {
                                    tvCardOption.setCardBackgroundColor(Color.parseColor("#E9F5E9"))
                                    tvCardOption.strokeColor = Color.parseColor("#48A145")
                                    checkBox.isChecked = true
                                    checkBox.setButtonDrawable(drawable.check_circle_correctoption)
                                    checkBox.buttonTintList =
                                        ColorStateList.valueOf(Color.parseColor("#48A145"))
                                }
                            }
                        }
                    }
                }

                OptionViewState.PARTIALLY_INCORRECT -> {
                    when (assessmentType) {
                        AssessmentType.multiple -> {
                            if (item.optionType == OptionType.text) {
                                ivImgOption.visibility = View.GONE
                                tvOption.visibility = View.VISIBLE
                                tvRadioButtonOption.visibility = View.GONE
                                checkBox.visibility = View.VISIBLE
                                if (item.attemptCount < 2) {
                                    checkBox.isChecked = false
                                    tvCardOption.setCardBackgroundColor(Color.parseColor("#F1F1F1"))
                                    tvCardOption.strokeColor = Color.parseColor("#F1F1F1")
                                    checkBox.buttonTintList =
                                        ColorStateList.valueOf(Color.parseColor("#000000"))
                                } else {
                                    tvCardOption.setCardBackgroundColor(Color.parseColor("#FFE5E3"))
                                    tvCardOption.strokeColor = Color.parseColor("#F44336")
                                    checkBox.isChecked = true
                                    checkBox.setButtonDrawable(drawable.cancel_circle_optionincorrect)
                                    checkBox.buttonTintList =
                                        ColorStateList.valueOf(Color.parseColor("#D63447"))
                                    tvCardOption.clOption.setOnTouchListener(null)
                                }
                            } else {
                                ivImgOption.visibility = View.VISIBLE
                                tvOption.visibility = View.GONE
                                checkBox.visibility = View.VISIBLE
                                tvRadioButtonOption.visibility = View.GONE
                                if (item.attemptCount < 2) {
                                    tvCardOption.setCardBackgroundColor(Color.parseColor("#F1F1F1"))
                                    tvCardOption.strokeColor = Color.parseColor("#F1F1F1")
                                    checkBox.isChecked = false
                                    checkBox.buttonTintList =
                                        ColorStateList.valueOf(Color.parseColor("#000000"))
                                } else {
                                    tvCardOption.setCardBackgroundColor(Color.parseColor("#FFE5E3"))
                                    tvCardOption.strokeColor = Color.parseColor("#F44336")
                                    checkBox.isChecked = true
                                    checkBox.setButtonDrawable(drawable.cancel_circle_optionincorrect)
                                    checkBox.buttonTintList =
                                        ColorStateList.valueOf(Color.parseColor("#D63447"))
                                }
                            }
                        }
                    }
                }
            }

            root.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    when (assessmentType) {
                        AssessmentType.multiple -> {
                            //repopulate the selected items list.
                            selectedOptions.clear()
                            selectedOptions.addAll(currentList.filter { it.viewState == OptionViewState.SELECTED })

                            val currItem = selectedOptions.find { it.id == item.id }
                            if (currItem != null) {
                                Log.d("TAG removing", "${selectedOptions.size}, $selectedOptions")
                                selectedOptions.remove(currItem)
                            } else {
                                Log.d("TAG adding", "${selectedOptions.size}, $selectedOptions")
                                selectedOptions.add(item)
                            }

                            callback?.invoke(selectedOptions)
                        }

                        AssessmentType.single -> {
                            selectedOptions.clear()
                            selectedOptions.add(item)
                            callback?.invoke(selectedOptions)
                        }
                    }

                }
            })

        }
    }

    private fun setVisibilityBasedOnAttemptCount(attemptCount: Int) {

    }

}