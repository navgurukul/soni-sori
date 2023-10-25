package org.navgurukul.learn.ui.learn.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.navgurukul.learn.R.*
import org.navgurukul.learn.courses.db.models.AssessmentType
import org.navgurukul.learn.courses.db.models.OptionResponse
import org.navgurukul.learn.courses.db.models.OptionViewState
import org.navgurukul.learn.courses.db.models.SolutionBaseCourseContent
import org.navgurukul.learn.databinding.ItemMcqOptionBinding
import org.navgurukul.learn.ui.common.DataBoundListAdapter

class OptionSelectionAdapter(val callback: ((OptionResponse) -> Unit)? = null):
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

    //for testing purpose
    private val assessmentType: AssessmentType = AssessmentType.single
//    private val solutionContentList:List<SolutionBaseCourseContent>?= null
//    private lateinit var solutionContentList:SolutionBaseCourseContent

    override fun bind(holder: DataBoundListAdapter.DataBoundViewHolder<ItemMcqOptionBinding>, item: OptionResponse) {
        val binding = holder.binding
        binding.tvOption.text = item.value

        binding.tvOption.text = HtmlCompat.fromHtml(
            item.value
                ?: "", HtmlCompat.FROM_HTML_MODE_COMPACT
        )

//        val assessmentType = solutionContentList?.get(0)?.assessmentType
//        val assessmentType = solutionContentList.assessmentType

        when(item.viewState){
            OptionViewState.SELECTED -> {
                if (assessmentType==AssessmentType.single){
                    binding.tvCardOption.setCardBackgroundColor(Color.parseColor("#E9F5E9"))
                    binding.tvCardOption.strokeColor = Color.parseColor("#48A145")
                    binding.tvRadioButtonOption.isChecked = true
                    binding.tvRadioButtonOption.buttonTintList = ColorStateList.valueOf(Color.parseColor("#48A145"))
                }else{
                    binding.tvCardOption.setCardBackgroundColor(Color.parseColor("#E9F5E9"))
                    binding.tvCardOption.strokeColor = Color.parseColor("#48A145")
                    binding.checkBox.isChecked = true
                    binding.checkBox.buttonTintList = ColorStateList.valueOf(Color.parseColor("#48A145"))
                }
            }
            OptionViewState.NOT_SELECTED -> {
                if (assessmentType==AssessmentType.single){
                    binding.tvCardOption.setCardBackgroundColor(Color.parseColor("#ffffff"))
                    binding.tvRadioButtonOption.visibility = View.VISIBLE
                }else{
                    binding.tvCardOption.setCardBackgroundColor(Color.parseColor("#ffffff"))
                    binding.tvRadioButtonOption.visibility = View.GONE
                    binding.checkBox.visibility = View.VISIBLE
                }
            }
            OptionViewState.INCORRECT -> {
                if (assessmentType==AssessmentType.single){
                    binding.tvCardOption.setCardBackgroundColor(Color.parseColor("#FFE5E3"))
                    binding.tvCardOption.strokeColor = Color.parseColor("#F44336")
                    binding.tvRadioButtonOption.isChecked = true
                    binding.tvRadioButtonOption.setButtonDrawable(drawable.cancel_circle_optionincorrect)
                    binding.tvRadioButtonOption.buttonTintList = ColorStateList.valueOf(Color.parseColor("#D63447"))
                }else{
                    binding.tvCardOption.setCardBackgroundColor(Color.parseColor("#FFE5E3"))
                    binding.tvCardOption.strokeColor = Color.parseColor("#F44336")
                    binding.checkBox.isChecked = true
                    binding.checkBox.setButtonDrawable(drawable.cancel_circle_optionincorrect)
                    binding.checkBox.buttonTintList = ColorStateList.valueOf(Color.parseColor("#D63447"))
                }

            }
            OptionViewState.CORRECT -> {
                if (assessmentType==AssessmentType.single){
                    binding.tvCardOption.setCardBackgroundColor(Color.parseColor("#E9F5E9"))
                    binding.tvCardOption.strokeColor = Color.parseColor("#48A145")
                    binding.tvRadioButtonOption.isChecked = true
                    binding.tvRadioButtonOption.setButtonDrawable(drawable.check_circle_correctoption)
                    binding.tvRadioButtonOption.buttonTintList = ColorStateList.valueOf(Color.parseColor("#48A145"))
                }else{
                    binding.tvCardOption.setCardBackgroundColor(Color.parseColor("#E9F5E9"))
                    binding.tvCardOption.strokeColor = Color.parseColor("#48A145")
                    binding.checkBox.isChecked = true
                    binding.checkBox.setButtonDrawable(drawable.check_circle_correctoption)
                    binding.checkBox.buttonTintList = ColorStateList.valueOf(Color.parseColor("#48A145"))
                }

            }
            OptionViewState.PARTIALLY_CORRECT ->{

            }
            OptionViewState.PARTIALLY_INCORRECT->{

            }
        }

        callback?.let {
            binding.root.setOnClickListener { view ->
                it.invoke(item)

            }
        }
    }

}