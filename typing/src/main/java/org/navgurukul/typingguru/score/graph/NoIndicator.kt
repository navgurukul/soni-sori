package org.navgurukul.typingguru.score.graph

import android.content.Context
import android.graphics.Canvas

class NoIndicator(context: Context) : Indicator<NoIndicator>(context) {

    override fun draw(canvas: Canvas, degree: Float) {}

    override fun updateIndicator() {}

    override fun setWithEffects(withEffects: Boolean) {}
}
