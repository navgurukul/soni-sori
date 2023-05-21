package org.navgurukul.chat.features.home.room.detail.timeline.tools

import android.text.SpannableStringBuilder
import android.text.style.ClickableSpan
import android.view.MotionEvent
import android.widget.TextView
import androidx.core.text.toSpannable
import org.matrix.android.sdk.api.session.permalinks.MatrixLinkify
import org.matrix.android.sdk.api.session.permalinks.MatrixPermalinkSpan
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.navgurukul.chat.core.linkify.SaralLinkify
import org.navgurukul.chat.core.utils.EvenBetterLinkMovementMethod
import org.navgurukul.chat.core.utils.isValidUrl
import org.navgurukul.chat.features.home.room.detail.timeline.TimelineEventController
import org.navgurukul.chat.features.html.PillImageSpan

fun CharSequence.findPillsAndProcess(scope: CoroutineScope, processBlock: (PillImageSpan) -> Unit) {
    scope.launch(Dispatchers.Main) {
        withContext(Dispatchers.IO) {
            toSpannable().let { spannable ->
                spannable.getSpans(0, spannable.length, PillImageSpan::class.java)
            }
        }.forEach { processBlock(it) }
    }
}

fun CharSequence.linkify(callback: TimelineEventController.UrlClickCallback?): CharSequence {
    val text = this.toString()
    val spannable = SpannableStringBuilder(this)
    MatrixLinkify.addLinks(spannable, object : MatrixPermalinkSpan.Callback {
        override fun onUrlClicked(url: String) {
            callback?.onUrlClicked(url, text)
        }
    })
    SaralLinkify.addLinks(spannable, true)
    return spannable
}

// Better link movement methods fixes the issue when
// long pressing to open the context menu on a TextView also triggers an autoLink click.
fun createLinkMovementMethod(urlClickCallback: TimelineEventController.UrlClickCallback?): EvenBetterLinkMovementMethod {
    return EvenBetterLinkMovementMethod(object : EvenBetterLinkMovementMethod.OnLinkClickListener {
        override fun onLinkClicked(textView: TextView, span: ClickableSpan, url: String, actualText: String): Boolean {
            // Always return false if the url is not valid, so the EvenBetterLinkMovementMethod can fallback to default click listener.
            return url.isValidUrl() && urlClickCallback?.onUrlClicked(url, actualText) == true
        }
    })
            .apply {
                // We need also to fix the case when long click on link will trigger long click on cell
                setOnLinkLongClickListener { tv, url ->
                    // Long clicks are handled by parent, return true to block android to do something with url
                    // Always return false if the url is not valid, so the EvenBetterLinkMovementMethod can fallback to default click listener.
                    if (url.isValidUrl() && urlClickCallback?.onUrlLongClicked(url) == true) {
                        tv.dispatchTouchEvent(MotionEvent.obtain(0, 0, MotionEvent.ACTION_CANCEL, 0f, 0f, 0))
                        true
                    } else {
                        false
                    }
                }
            }
}
