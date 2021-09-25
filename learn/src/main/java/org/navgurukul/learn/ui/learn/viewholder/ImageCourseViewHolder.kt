package org.navgurukul.learn.ui.learn.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.ImageBaseCourseContent
import org.navgurukul.learn.databinding.ItemBaseCourseContentBinding
import org.navgurukul.learn.databinding.ItemImageContentBinding
import org.navgurukul.learn.ui.learn.adapter.ExerciseContentAdapter


class ImageCourseViewHolder(itemBinding: ItemBaseCourseContentBinding) :
    BaseCourseViewHolder(itemBinding) {

    val binding: ItemImageContentBinding

    init {
        binding = DataBindingUtil.inflate<ItemImageContentBinding>(
            LayoutInflater.from(itemBinding.root.context),
            R.layout.item_image_content, itemBinding.root as ViewGroup, false
        )
        super.addView(binding.root)
        super.addPlaceholder(binding.root.id)
    }

    fun bindView(item: ImageBaseCourseContent) {
        super.bind(item)

        item.value?.let { url ->
            binding.imageView.visibility = View.VISIBLE
            Glide.with(binding.imageView.context).load(url).into(binding.imageView);
        }
        binding.imageView.contentDescription = item.alt

    }

}

