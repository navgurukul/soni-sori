package org.navgurukul.learn.ui.learn.viewholder

import android.content.Intent
import android.view.View
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.YoutubeBaseCourseContent
import org.navgurukul.learn.ui.learn.YoutubePlayerActivity

class YoutubeCourseViewHolder(itemView: View) :
    BaseCourseViewHolder(itemView) {

    private val youtubeView: YouTubePlayerView = populateStub(R.layout.item_youtube_content)

    override val horizontalMargin: Int
        get() = 0

    init {
        super.setHorizontalMargin(horizontalMargin)
    }

    fun bindView(item: YoutubeBaseCourseContent) {
        super.bind(item)

        youtubeView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                item.value?.let { youTubePlayer.cueVideo(it, 0f) }
            }
        })

        youtubeView.getPlayerUiController().setFullScreenButtonClickListener {
            val context = itemView.context
            val intent = Intent(context, YoutubePlayerActivity::class.java)
            intent.putExtra(YoutubePlayerActivity.EXTRA_VIDEO_ID, item.value)
            context.startActivity(intent)
        }
    }
}
