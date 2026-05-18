package org.navgurukul.learn.ui.learn.viewholder

import android.content.res.Resources
import android.view.View
import android.widget.ImageView
import android.util.Log
import android.content.Intent
import android.net.Uri
import android.content.ActivityNotFoundException
import android.webkit.CookieManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import androidx.lifecycle.LifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.bumptech.glide.Glide
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.YoutubeBaseCourseContent

class YoutubeCourseViewHolder(itemView: View) :
    BaseCourseViewHolder(itemView) {
    private val youtubeContainer: View = populateStub(R.layout.item_youtube_content)
    private val youtubeView: YouTubePlayerView = youtubeContainer.findViewById(R.id.youtube_view)
    private val thumbnailView: ImageView = youtubeContainer.findViewById(R.id.youtube_thumbnail)

    private val TAG = "YoutubeCourseViewHolder"

    private var youTubePlayer: YouTubePlayer? = null
    private var playerReady: Boolean = false
    private var pendingVideoId: String? = null
    private var currentVideoId: String? = null  // Store video ID for click fallback
    private var manualFallbackEnabled: Boolean = false

    private val mainHandler = Handler(Looper.getMainLooper())
    private var playerInitTimeout: Runnable? = null
    private val PLAYER_INIT_TIMEOUT_MS = 5000L

    val childLayoutParams = youtubeContainer.layoutParams

    override val horizontalMargin: Int
        get() = 0

    init {
        super.setHorizontalMargin(horizontalMargin)
        
        // Enable cookies for YouTube embed (maybe needed for certain videos/regions)
        try {
            val cm = CookieManager.getInstance()
            cm.setAcceptCookie(true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // so we enable cookies globally via CookieManager
                Log.d(TAG, "Cookies enabled globally for YouTube")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Could not enable cookies: ${e.message}")
        }
        
        val lifecycleOwner = itemView.context as? LifecycleOwner
        if (lifecycleOwner != null) {
            lifecycleOwner.lifecycle.addObserver(youtubeView)
            Log.d(TAG, "YouTubePlayerView lifecycle observer registered")
        } else {
            Log.w(TAG, "Could not find LifecycleOwner for YouTubePlayerView")
        }

        // NOTE: container height will be set when the view is bound (in bindView)

        youtubeView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(player: YouTubePlayer) {
                Log.d(TAG, "✅ YouTube Player READY - ready to load videos")
                youTubePlayer = player
                playerReady = true
                playerInitTimeout?.let { mainHandler.removeCallbacks(it) }
                playerInitTimeout = null
                pendingVideoId?.let {
                    val id = it.trim()
                    Log.d(TAG, "Loading pending video in ready player: $id")
                    try {
                        youTubePlayer?.cueVideo(id, 0f)
                        Log.d(TAG, "✅ Successfully cued video: $id in embedded player")
                        thumbnailView.visibility = View.GONE
                    } catch (e: Exception) {
                        Log.w(TAG, "❌ Failed to cue pending video $id: ${e.message}")
                    }
                    pendingVideoId = null
                }
            }

            override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                Log.e(TAG, "❌ YouTube player error: $error")
                pendingVideoId?.let { 
                    manualFallbackEnabled = true
                    Log.d(TAG, "Embedding failed for $it, waiting for user tap to open YouTube")
                    Log.d(TAG, "💡 User can tap the video area to open in YouTube app")
                }
            }
        })
        
        val openOnTap = View.OnClickListener {
            currentVideoId?.let { id ->
                if (playerReady && youTubePlayer != null && !manualFallbackEnabled) {
                    Log.d(TAG, "📺 Player ready — attempting in-app play: $id")
                    try {
                        youTubePlayer?.loadVideo(id, 0f)
                        thumbnailView.visibility = View.GONE
                    } catch (e: Exception) {
                        Log.w(TAG, "⚠️ In-app play failed for $id: ${e.message}, falling back to external")
                        manualFallbackEnabled = true
                        openExternal(id)
                    }
                } else {
                    Log.d(TAG, "📲 Opening externally (user tap): $id")
                    openExternal(id)
                    manualFallbackEnabled = false
                }
            }
        }
        youtubeContainer.setOnClickListener(openOnTap)
        thumbnailView.setOnClickListener(openOnTap)
    }

    fun bindView(item: YoutubeBaseCourseContent) {
        super.bind(item)
        val videoId = item.value
        if (videoId.isNullOrBlank()) {
            Log.w(TAG, "❌ ERROR: YouTube video ID is null or blank!")
            return
        }

        val sanitizedId = videoId.trim()
        currentVideoId = sanitizedId  // Store for click listener
        manualFallbackEnabled = false

        thumbnailView.visibility = View.VISIBLE
        val thumbnailUrl = "https://img.youtube.com/vi/$sanitizedId/hqdefault.jpg"
        Glide.with(itemView.context)
            .load(thumbnailUrl)
            .placeholder(android.R.color.black)
            .error(android.R.color.black)
            .into(thumbnailView)

        val displayMetrics = itemView.resources.displayMetrics
        val fixedHeightDp = 280
        val fixedHeightPx = (fixedHeightDp * displayMetrics.density).toInt()
        youtubeContainer.layoutParams?.let { lp ->
            lp.height = fixedHeightPx
            youtubeContainer.layoutParams = lp
        }
        youtubeContainer.requestLayout()

        try {
            thumbnailView.scaleType = ImageView.ScaleType.FIT_CENTER
            thumbnailView.adjustViewBounds = false
        } catch (e: Exception) {
            Log.w(TAG, "Could not set thumbnail scaleType: ${e.message}")
        }

        if (playerReady && youTubePlayer != null) {
            Log.d(TAG, "📺 Player ready, loading video in embedded player: $sanitizedId")
            try {
                youTubePlayer?.cueVideo(sanitizedId, 0f)
                Log.d(TAG, "✅ Successfully cued video: $sanitizedId in app")
                thumbnailView.visibility = View.GONE
            } catch (e: Exception) {
                Log.w(TAG, "⚠️  cueVideo failed for $sanitizedId: ${e.message}, trying fallback...")
                try {
                    youTubePlayer?.loadVideo(sanitizedId, 0f)
                    Log.d(TAG, "✅ Loaded video via loadVideo fallback: $sanitizedId")
                    thumbnailView.visibility = View.GONE
                } catch (e2: Exception) {
                    Log.e(TAG, "❌ Both cueVideo and loadVideo failed: ${e2.message}")
                }
            }
        } else {
            Log.d(TAG, "⏳ Player not ready yet, queuing video: $sanitizedId (will load when player is ready)")
            pendingVideoId = sanitizedId
            playerInitTimeout?.let { mainHandler.removeCallbacks(it) }
            playerInitTimeout = Runnable {
                Log.w(TAG, "⏱️  Player init timeout after ${PLAYER_INIT_TIMEOUT_MS}ms for: $sanitizedId - video will show blank, tap to open in YouTube")
                playerInitTimeout = null
                manualFallbackEnabled = true
                Log.d(TAG, "💡 User can tap the blank area to open in YouTube app")
            }
            mainHandler.postDelayed(playerInitTimeout!!, PLAYER_INIT_TIMEOUT_MS)
            Log.d(TAG, "⏱️  Timeout scheduled (${PLAYER_INIT_TIMEOUT_MS}ms)")
        }

    }

    fun setViewHeight(widthHeightRatio: Double) {
        val width = Resources.getSystem().getDisplayMetrics().widthPixels
        val heightPx = (width / widthHeightRatio).toInt()
        
        val containerParams = youtubeContainer.layoutParams
        if (containerParams != null) {
            containerParams.height = heightPx
            youtubeContainer.layoutParams = containerParams
        }
    }

    private fun openExternal(videoId: String) {
        Log.d(TAG, "🔗 Embedding failed, opening video externally: $videoId")
        Log.d(TAG, "Trying YouTube app first, then browser fallback")
        val ctx = itemView.context
        try {
            val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoId"))
            ctx.startActivity(appIntent)
            Log.d(TAG, "✅ Opened in YouTube app")
        } catch (e: ActivityNotFoundException) {
            try {
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoId"))
                ctx.startActivity(webIntent)
                Log.d(TAG, "✅ Opened in browser")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Failed to open video in browser: ${e.message}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error opening YouTube app: ${e.message}")
        }
    }

}
