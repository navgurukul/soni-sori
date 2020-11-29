package org.navgurukul.chat.features.media

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.net.toUri
import kotlinx.android.synthetic.main.activity_big_image_viewer.*
import org.koin.android.ext.android.inject
import org.navgurukul.chat.ChatBaseActivity
import org.navgurukul.chat.R
import org.navgurukul.chat.core.glide.GlideApp
import org.navgurukul.chat.core.repo.ActiveSessionHolder

/**
 * Simple Activity to display an avatar in fullscreen
 */
class BigImageViewerActivity : ChatBaseActivity() {
    val sessionHolder: ActiveSessionHolder by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_big_image_viewer)

        setSupportActionBar(bigImageViewerToolbar)
        supportActionBar?.apply {
            title = intent.getStringExtra(EXTRA_TITLE)
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        val uri = sessionHolder.getSafeActiveSession()
                ?.contentUrlResolver()
                ?.resolveFullSize(intent.getStringExtra(EXTRA_IMAGE_URL))
                ?.toUri()

        if (uri == null) {
            finish()
        } else {
            GlideApp.with(imageView)
                .load(uri)
                .into(imageView)
        }
    }

    companion object {
        private const val EXTRA_TITLE = "EXTRA_TITLE"
        private const val EXTRA_IMAGE_URL = "EXTRA_IMAGE_URL"

        fun newIntent(context: Context, title: String?, imageUrl: String): Intent {
            return Intent(context, BigImageViewerActivity::class.java).apply {
                putExtra(EXTRA_TITLE, title)
                putExtra(EXTRA_IMAGE_URL, imageUrl)
            }
        }
    }
}
