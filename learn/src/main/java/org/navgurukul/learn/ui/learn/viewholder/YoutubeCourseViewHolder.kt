package org.navgurukul.learn.ui.learn.viewholder

import android.view.View
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.YoutubeBaseCourseContent

class YoutubeCourseViewHolder(itemView: View) :
    BaseCourseViewHolder(itemView) {
    private val youtubeView: YouTubePlayerView = populateStub(R.layout.item_youtube_content)

    override val horizontalMargin: Int
        get() = youtubeView.context.resources.getDimensionPixelOffset(R.dimen.spacing_4x)

    init {
        super.setHorizontalMargin(horizontalMargin)
    }

    fun bindView(item: YoutubeBaseCourseContent) {
        super.bind(item)
        youtubeView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.cueVideo(item.value, 0f)
            }
        })

    }

}
