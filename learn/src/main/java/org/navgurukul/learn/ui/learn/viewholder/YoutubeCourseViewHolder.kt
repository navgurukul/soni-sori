package org.navgurukul.learn.ui.learn.viewholder

import android.content.res.Resources
import android.view.View
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.YoutubeBaseCourseContent

class YoutubeCourseViewHolder(itemView: View) :
    BaseCourseViewHolder(itemView) {
    private val youtubeView: YouTubePlayerView = populateStub(R.layout.item_youtube_content)

    val childLayoutParams = youtubeView.layoutParams

    override val horizontalMargin: Int
        get() = 0

    init {
        super.setHorizontalMargin(horizontalMargin)
        setViewHeight(1.33)
    }

    fun bindView(item: YoutubeBaseCourseContent) {
        super.bind(item)
        youtubeView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                item.value?.let { youTubePlayer.cueVideo(it, 0f) }
            }
        })

    }

    fun setViewHeight(widthHeightRatio: Double) {
        childLayoutParams.height = (Resources.getSystem().getDisplayMetrics().widthPixels/widthHeightRatio).toInt()
    }

}
