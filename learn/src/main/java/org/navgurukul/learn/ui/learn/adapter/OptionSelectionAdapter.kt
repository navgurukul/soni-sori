package org.navgurukul.learn.ui.learn.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import org.navgurukul.learn.R.*
import org.navgurukul.learn.courses.db.models.*
import org.navgurukul.learn.databinding.ItemMcqOptionBinding
import org.navgurukul.learn.ui.common.DataBoundListAdapter

class OptionSelectionAdapter(
    val callback: ((List<OptionResponse>) -> Unit)? = null,
    private val assessmentType: AssessmentType? = null
):
    DataBoundListAdapter<OptionResponse, ItemMcqOptionBinding>(
        mDiffCallback = object : DiffUtil.ItemCallback<OptionResponse>(){

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
        })
{
    override fun createBinding(parent: ViewGroup, viewType: Int): ItemMcqOptionBinding{
       return DataBindingUtil.inflate(
           LayoutInflater.from(parent.context),
           layout.item_mcq_option,parent,false
       )
    }

    private val selectedOptions = mutableListOf<OptionResponse>()

    override fun bind(holder: DataBoundListAdapter.DataBoundViewHolder<ItemMcqOptionBinding>, item: OptionResponse) {
        val binding = holder.binding
        binding.apply {
            if (item.optionType == OptionType.text){
                tvOption.visibility = View.VISIBLE
                ivImgOption.visibility = View.GONE
                tvOption.text = item.value
                tvOption.text = HtmlCompat.fromHtml(item.value, HtmlCompat.FROM_HTML_MODE_COMPACT)
            }else{
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


            when(item.viewState){
                OptionViewState.SELECTED -> {
                    when (assessmentType){
                        AssessmentType.single ->{
                            if (item.optionType == OptionType.text){
                                ivImgOption.visibility = View.GONE
                                tvOption.visibility = View.VISIBLE
                                tvCardOption.setCardBackgroundColor(Color.parseColor("#E9F5E9"))
                                tvCardOption.strokeColor = Color.parseColor("#48A145")
                                tvRadioButtonOption.isChecked = true
                                tvRadioButtonOption.buttonTintList = ColorStateList.valueOf(Color.parseColor("#48A145"))
                            }else{
                                ivImgOption.visibility = View.VISIBLE
                                tvOption.visibility = View.GONE
                                tvCardOption.setCardBackgroundColor(Color.parseColor("#E9F5E9"))
                                tvCardOption.strokeColor = Color.parseColor("#48A145")
                                tvRadioButtonOption.isChecked = true
                                tvRadioButtonOption.buttonTintList = ColorStateList.valueOf(Color.parseColor("#48A145"))
                            }

                        }AssessmentType.multiple ->{
                            if (item.optionType == OptionType.text){
                                ivImgOption.visibility = View.GONE
                                tvOption.visibility = View.VISIBLE
                                tvCardOption.setCardBackgroundColor(Color.parseColor("#E9F5E9"))
                                tvCardOption.strokeColor = Color.parseColor("#48A145")
                                checkBox.isChecked = true
                                checkBox.visibility = View.VISIBLE
                                tvRadioButtonOption.visibility = View.GONE
                                checkBox.buttonTintList = ColorStateList.valueOf(Color.parseColor("#48A145"))
                            }else{
                                ivImgOption.visibility = View.VISIBLE
                                tvOption.visibility = View.GONE
                                tvCardOption.setCardBackgroundColor(Color.parseColor("#E9F5E9"))
                                tvCardOption.strokeColor = Color.parseColor("#48A145")
                                checkBox.isChecked = true
                                checkBox.visibility = View.VISIBLE
                                tvRadioButtonOption.visibility = View.GONE
                                checkBox.buttonTintList = ColorStateList.valueOf(Color.parseColor("#48A145"))
                            }

                        }
                    }
                }
                OptionViewState.NOT_SELECTED -> {
                    when(assessmentType){
                        AssessmentType.single -> {
                            checkBox.visibility = View.GONE
                            if (item.optionType == OptionType.text) {
                                ivImgOption.visibility = View.GONE
                                tvOption.visibility = View.VISIBLE
                                tvCardOption.setCardBackgroundColor(Color.parseColor("#ffffff"))
                                tvRadioButtonOption.visibility = View.VISIBLE
                            } else {
                                ivImgOption.visibility = View.VISIBLE
                                tvOption.visibility = View.GONE
                                tvCardOption.setCardBackgroundColor(Color.parseColor("#ffffff"))
                                tvRadioButtonOption.visibility = View.VISIBLE
                            }
                        }
                        AssessmentType.multiple -> {
                            tvRadioButtonOption.visibility = View.GONE
                            if (item.optionType == OptionType.text) {
                                ivImgOption.visibility = View.GONE
                                tvOption.visibility = View.VISIBLE
                                tvCardOption.setCardBackgroundColor(Color.parseColor("#ffffff"))
                                tvCardOption.strokeColor = Color.parseColor("#ffffff")
                                checkBox.visibility = View.VISIBLE
                                checkBox.isChecked = false
                                checkBox.buttonTintList =
                                    ColorStateList.valueOf(Color.parseColor("#000000"))
                            } else {
                                ivImgOption.visibility = View.VISIBLE
                                tvOption.visibility = View.GONE
                                tvCardOption.setCardBackgroundColor(Color.parseColor("#ffffff"))
                                tvCardOption.strokeColor = Color.parseColor("#ffffff")
                                checkBox.buttonTintList =
                                    ColorStateList.valueOf(Color.parseColor("#000000"))
                                checkBox.visibility = View.VISIBLE
                                checkBox.isChecked = false
                            }
                        }
                    }

                }
                OptionViewState.INCORRECT -> {
                    when (assessmentType){
                        AssessmentType.single ->{
                            if (item.optionType == OptionType.text){
                                ivImgOption.visibility = View.GONE
                                tvOption.visibility = View.VISIBLE
                                tvCardOption.setCardBackgroundColor(Color.parseColor("#FFE5E3"))
                                tvCardOption.strokeColor = Color.parseColor("#F44336")
                                tvRadioButtonOption.isChecked = true
                                tvRadioButtonOption.setButtonDrawable(drawable.cancel_circle_optionincorrect)
                                tvRadioButtonOption.buttonTintList = ColorStateList.valueOf(Color.parseColor("#D63447"))
                            }else{
                                ivImgOption.visibility = View.VISIBLE
                                tvOption.visibility = View.GONE
                                tvCardOption.setCardBackgroundColor(Color.parseColor("#FFE5E3"))
                                tvCardOption.strokeColor = Color.parseColor("#F44336")
                                tvRadioButtonOption.isChecked = true
                                tvRadioButtonOption.setButtonDrawable(drawable.cancel_circle_optionincorrect)
                                tvRadioButtonOption.buttonTintList = ColorStateList.valueOf(Color.parseColor("#D63447"))
                            }
                        }
                        AssessmentType.multiple -> {
                            if (item.optionType == OptionType.text){
                                ivImgOption.visibility = View.GONE
                                tvOption.visibility = View.VISIBLE
                                tvCardOption.setCardBackgroundColor(Color.parseColor("#FFE5E3"))
                                tvCardOption.strokeColor = Color.parseColor("#F44336")
                                checkBox.isChecked = true
                                checkBox.setButtonDrawable(drawable.cancel_circle_optionincorrect)
                                checkBox.buttonTintList = ColorStateList.valueOf(Color.parseColor("#D63447"))
                            }else{
                                ivImgOption.visibility = View.VISIBLE
                                tvOption.visibility = View.GONE
                                tvCardOption.setCardBackgroundColor(Color.parseColor("#FFE5E3"))
                                tvCardOption.strokeColor = Color.parseColor("#F44336")
                                checkBox.isChecked = true
                                checkBox.setButtonDrawable(drawable.cancel_circle_optionincorrect)
                                checkBox.buttonTintList = ColorStateList.valueOf(Color.parseColor("#D63447"))
                            }
                        }
                    }
                }
                OptionViewState.CORRECT -> {
                    when (assessmentType){
                        AssessmentType.single ->{
                            if (item.optionType == OptionType.text){
                                ivImgOption.visibility = View.GONE
                                tvOption.visibility = View.VISIBLE
                                tvCardOption.setCardBackgroundColor(Color.parseColor("#E9F5E9"))
                                tvCardOption.strokeColor = Color.parseColor("#48A145")
                                tvRadioButtonOption.isChecked = true
                                tvRadioButtonOption.setButtonDrawable(drawable.check_circle_correctoption)
                                tvRadioButtonOption.buttonTintList = ColorStateList.valueOf(Color.parseColor("#48A145"))
                            }else{
                                ivImgOption.visibility = View.VISIBLE
                                tvOption.visibility = View.GONE
                                tvCardOption.setCardBackgroundColor(Color.parseColor("#E9F5E9"))
                                tvCardOption.strokeColor = Color.parseColor("#48A145")
                                tvRadioButtonOption.isChecked = true
                                tvRadioButtonOption.setButtonDrawable(drawable.check_circle_correctoption)
                                tvRadioButtonOption.buttonTintList = ColorStateList.valueOf(Color.parseColor("#48A145"))
                            }
                        }
                        AssessmentType.multiple -> {
                            if (item.optionType == OptionType.text){
                                ivImgOption.visibility = View.GONE
                                tvOption.visibility = View.VISIBLE
                                tvCardOption.setCardBackgroundColor(Color.parseColor("#E9F5E9"))
                                tvCardOption.strokeColor = Color.parseColor("#48A145")
                                checkBox.isChecked = true
                                checkBox.setButtonDrawable(drawable.check_circle_correctoption)
                                checkBox.buttonTintList = ColorStateList.valueOf(Color.parseColor("#48A145"))
                            }else{
                                ivImgOption.visibility = View.VISIBLE
                                tvOption.visibility = View.GONE
                                tvCardOption.setCardBackgroundColor(Color.parseColor("#E9F5E9"))
                                tvCardOption.strokeColor = Color.parseColor("#48A145")
                                checkBox.isChecked = true
                                checkBox.setButtonDrawable(drawable.check_circle_correctoption)
                                checkBox.buttonTintList = ColorStateList.valueOf(Color.parseColor("#48A145"))
                            }
                        }
                    }
                }
                OptionViewState.PARTIALLY_CORRECT ->{
                    when (assessmentType){
                        AssessmentType.multiple -> {
                            if (item.optionType == OptionType.text){
                                ivImgOption.visibility = View.GONE
                                tvOption.visibility = View.VISIBLE
                                tvCardOption.setCardBackgroundColor(Color.parseColor("#E9F5E9"))
                                tvCardOption.strokeColor = Color.parseColor("#48A145")
                                checkBox.isChecked = true
                                checkBox.setButtonDrawable(drawable.check_circle_correctoption)
                                checkBox.buttonTintList = ColorStateList.valueOf(Color.parseColor("#FFC107"))
                            }else{
                                ivImgOption.visibility = View.VISIBLE
                                tvOption.visibility = View.GONE
                                tvCardOption.setCardBackgroundColor(Color.parseColor("#FFFDE7"))
                                tvCardOption.strokeColor = Color.parseColor("#FFC107")
                                checkBox.isChecked = true
                                checkBox.setButtonDrawable(drawable.check_circle_correctoption)
                                checkBox.buttonTintList = ColorStateList.valueOf(Color.parseColor("#FFC107"))
                            }
                        }
                    }
                }
                OptionViewState.PARTIALLY_INCORRECT->{
                    when (assessmentType){
                        AssessmentType.multiple -> {
                            if (item.optionType == OptionType.text){
                                ivImgOption.visibility = View.GONE
                                tvOption.visibility = View.VISIBLE
                                tvCardOption.setCardBackgroundColor(Color.parseColor("#FFE5E3"))
                                tvCardOption.strokeColor = Color.parseColor("#F44336")
                                checkBox.isChecked = true
                                checkBox.setButtonDrawable(drawable.cancel_circle_optionincorrect)
                                checkBox.buttonTintList = ColorStateList.valueOf(Color.parseColor("#D63447"))
                            }else{
                                ivImgOption.visibility = View.VISIBLE
                                tvOption.visibility = View.GONE
                                tvCardOption.setCardBackgroundColor(Color.parseColor("#FFE5E3"))
                                tvCardOption.strokeColor = Color.parseColor("#F44336")
                                checkBox.isChecked = true
                                checkBox.setButtonDrawable(drawable.cancel_circle_optionincorrect)
                                checkBox.buttonTintList = ColorStateList.valueOf(Color.parseColor("#D63447"))
                            }
                        }
                }
            }
            }


            root.setOnClickListener {
                when(assessmentType){
                    AssessmentType.multiple -> { if (selectedOptions.contains(item)) {
                            selectedOptions.remove(item)
                            item.viewState = OptionViewState.NOT_SELECTED
                        } else {
                            selectedOptions.add(item)
                            item.viewState = OptionViewState.SELECTED
                        }
                        bind(holder, item)
                        callback?.invoke(selectedOptions)
                    }
                    AssessmentType.single -> {
                        selectedOptions.clear()
                        selectedOptions.add(item)
                        callback?.invoke(selectedOptions)
                    }
                }
            }

        }
    }

}