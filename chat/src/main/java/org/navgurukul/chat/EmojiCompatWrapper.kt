package org.navgurukul.chat

import android.content.Context
import androidx.core.provider.FontRequest
import androidx.emoji.text.EmojiCompat
import androidx.emoji.text.FontRequestEmojiCompatConfig
import timber.log.Timber

class EmojiCompatWrapper (private val context: Context)  {

    private var initialized = false

    fun init(fontRequest: FontRequest) {
        // Use emoji compat for the benefit of emoji spans
        val config = FontRequestEmojiCompatConfig(context, fontRequest)
                // we want to replace all emojis with selected font
                .setReplaceAll(true)
        // Debug options
//                .setEmojiSpanIndicatorEnabled(true)
//                .setEmojiSpanIndicatorColor(Color.GREEN)
        EmojiCompat.init(config)
                .registerInitCallback(object : EmojiCompat.InitCallback() {
                    override fun onInitialized() {
                        Timber.v("Emoji compat onInitialized success ")
                        initialized = true
                    }

                    override fun onFailed(throwable: Throwable?) {
                        Timber.e(throwable, "Failed to init EmojiCompat")
                    }
                })
    }

    fun safeEmojiSpanify(sequence: CharSequence): CharSequence {
        if (initialized) {
            try {
                return EmojiCompat.get().process(sequence)
            } catch (throwable: Throwable) {
                // Defensive coding against error (should not happend as it is initialized)
                Timber.e(throwable, "Failed to init EmojiCompat")
                return sequence
            }
        } else {
            return sequence
        }
    }
}
