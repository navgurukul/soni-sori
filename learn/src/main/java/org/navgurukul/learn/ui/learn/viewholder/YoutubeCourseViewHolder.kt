package org.navgurukul.learn.ui.learn.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.YoutubeBaseCourseContent
import org.navgurukul.learn.databinding.ItemBaseCourseContentBinding
import org.navgurukul.learn.databinding.ItemYoutubeContentBinding
import org.navgurukul.learn.ui.learn.adapter.ExerciseContentAdapter

class YoutubeCourseViewHolder(itemBinding: ItemBaseCourseContentBinding) :
    BaseCourseViewHolder(itemBinding) {
    val binding: ItemYoutubeContentBinding

    init {
        binding = DataBindingUtil.inflate<ItemYoutubeContentBinding>(
            LayoutInflater.from(itemBinding.root.context),
            R.layout.item_youtube_content, itemBinding.root as ViewGroup, false
        )
        super.addView(binding.root)
        super.addPlaceholder(binding.root.id)

    }

    fun bindView(item: YoutubeBaseCourseContent) {
        super.bind(item)


        binding.youtubeView.visibility = View.VISIBLE

        binding.youtubeView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer) {
                item.value?.let { youTubePlayer.cueVideo(it, 0f) }
            }
        })

    }

}
