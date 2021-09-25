package org.navgurukul.learn.ui.learn.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.BannerAction
import org.navgurukul.learn.courses.db.models.BannerBaseCourseContent
import org.navgurukul.learn.databinding.ItemBannerContentBinding
import org.navgurukul.learn.databinding.ItemBaseCourseContentBinding
import org.navgurukul.learn.ui.learn.adapter.ExerciseContentAdapter

class BannerCourseViewHolder(itemBinding: ItemBaseCourseContentBinding) :
    BaseCourseViewHolder(itemBinding) {
    val binding: ItemBannerContentBinding

    init {
        binding = DataBindingUtil.inflate<ItemBannerContentBinding>(
            LayoutInflater.from(itemBinding.root.context),
            R.layout.item_banner_content, itemBinding.root as ViewGroup, false
        )
        super.addView(binding.root)
        super.addPlaceholder(binding.root.id)

    }

    fun bindView(item: BannerBaseCourseContent, urlCallback: (BannerAction?) -> Unit) {
        super.bind(item)

        binding.layoutBannerContent.visibility = View.VISIBLE

        item.actions?.let {
            setActionButtons(binding, it, urlCallback)
        }
        binding.bannerTitle.text = item.title
        binding.bannerBody.text = item.value
    }

    private fun setActionButtons(
        binding: ItemBannerContentBinding,
        actions: List<BannerAction>,
        urlCallback: (BannerAction?) -> Unit
    ) {
        actions.forEachIndexed { index, element ->
            if (index == 0) {
                binding.bannerButton2.visibility = View.GONE

                binding.bannerButton1.visibility = View.VISIBLE
                binding.bannerButton1.text = element.label
                binding.bannerButton1.setOnClickListener {
                    urlCallback.invoke(element)
                }
            } else {
                binding.bannerButton2.visibility = View.VISIBLE
                binding.bannerButton2.text = element.label
                binding.bannerButton2.setOnClickListener {
                    urlCallback.invoke(element)
                }
            }
        }
    }

}

