package org.navgurukul.learn.ui.learn

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import org.navgurukul.learn.R

class YoutubePlayerActivity : Activity() {

    private lateinit var youtubeView: YouTubePlayerView
    private var videoId: String? = null

    companion object {
        const val EXTRA_VIDEO_ID = "video_id"
    }

    private var isFullscreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtube_player)

        youtubeView = findViewById(R.id.youtube_view)
        videoId = intent.getStringExtra(EXTRA_VIDEO_ID)

        youtubeView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                videoId?.let { youTubePlayer.loadVideo(it, 0f) }
            }
        })

        enterFullscreen()
    }

    override fun onDestroy() {
        exitFullscreen()
        super.onDestroy()
    }

    private fun enterFullscreen() {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        youtubeView.enterFullScreen()
        isFullscreen = true
    }

    private fun exitFullscreen() {
        if (isFullscreen) {
            youtubeView.toggleFullScreen()
            isFullscreen = false
        }
    }

    override fun onBackPressed() {
        if (isFullscreen) {
            exitFullscreen()
        } else {
            super.onBackPressed()
        }
    }
}



