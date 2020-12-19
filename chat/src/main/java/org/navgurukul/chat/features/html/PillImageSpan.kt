package org.navgurukul.chat.features.html

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.style.ReplacementSpan
import android.view.ContextThemeWrapper
import android.widget.TextView
import androidx.annotation.UiThread
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.chip.ChipDrawable
import org.matrix.android.sdk.api.session.room.send.MatrixItemSpan
import org.matrix.android.sdk.api.util.MatrixItem
import org.navgurukul.chat.R
import org.navgurukul.chat.core.glide.GlideRequests
import org.navgurukul.chat.features.home.AvatarRenderer
import java.lang.ref.WeakReference

/**
 * This span is able to replace a text by a [ChipDrawable]
 * It's needed to call [bind] method to start requesting avatar, otherwise only the placeholder icon will be displayed if not already cached.
 * Implements MatrixItemSpan so that it could be automatically transformed in matrix links and displayed as pills.
 */
class PillImageSpan(private val glideRequests: GlideRequests,
                    private val avatarRenderer: AvatarRenderer,
                    private val context: Context,
                    override val matrixItem: MatrixItem
) : ReplacementSpan(), MatrixItemSpan {

    private val pillDrawable = createChipDrawable()
    private val target = PillImageSpanTarget(this)
    private var tv: WeakReference<TextView>? = null

    @UiThread
    fun bind(textView: TextView) {
        tv = WeakReference(textView)
        avatarRenderer.render(context, glideRequests, matrixItem, target)
    }

    // ReplacementSpan *****************************************************************************

    override fun getSize(paint: Paint, text: CharSequence,
                         start: Int,
                         end: Int,
                         fm: Paint.FontMetricsInt?): Int {
        val rect = pillDrawable.bounds
        if (fm != null) {
            fm.ascent = -rect.bottom
            fm.descent = 0
            fm.top = fm.ascent
            fm.bottom = 0
        }
        return rect.right
    }

    override fun draw(canvas: Canvas, text: CharSequence,
                      start: Int,
                      end: Int,
                      x: Float,
                      top: Int,
                      y: Int,
                      bottom: Int,
                      paint: Paint) {
        canvas.save()
        val transY = bottom - pillDrawable.bounds.bottom
        canvas.translate(x, transY.toFloat())
        pillDrawable.draw(canvas)
        canvas.restore()
    }

    internal fun updateAvatarDrawable(drawable: Drawable?) {
        pillDrawable.chipIcon = drawable
        tv?.get()?.invalidate()
    }

    // Private methods *****************************************************************************

    private fun createChipDrawable(): ChipDrawable {
        val textPadding = context.resources.getDimension(R.dimen.spacing_1x)
        val icon = try {
            avatarRenderer.getCachedDrawable(glideRequests, matrixItem)
        } catch (exception: Exception) {
            avatarRenderer.getPlaceholderDrawable(context, matrixItem)
        }

        return ChipDrawable.createFromResource(ContextThemeWrapper(context, R.style.AppTheme), R.xml.pill_view).apply {
            text = matrixItem.getBestName()
            textEndPadding = textPadding
            textStartPadding = textPadding
            setChipMinHeightResource(R.dimen.pill_min_height)
            setChipIconSizeResource(R.dimen.pill_avatar_size)
            chipIcon = icon
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        }
    }
}

/**
 * Glide target to handle avatar retrieval into [PillImageSpan].
 */
private class PillImageSpanTarget(pillImageSpan: PillImageSpan) : SimpleTarget<Drawable>() {

    private val pillImageSpan = WeakReference(pillImageSpan)

    override fun onResourceReady(drawable: Drawable, transition: Transition<in Drawable>?) {
        updateWith(drawable)
    }

    override fun onLoadCleared(placeholder: Drawable?) {
        updateWith(placeholder)
    }

    private fun updateWith(drawable: Drawable?) {
        pillImageSpan.get()?.apply {
            updateAvatarDrawable(drawable)
        }
    }
}