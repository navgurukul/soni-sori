package org.navgurukul.learn.ui.learn.viewholder

import android.view.View
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.UnknownBaseCourseContent

class UnknownCourseViewHolder(itemView: View) :
    BaseCourseViewHolder(itemView) {

    override val horizontalMargin: Int
        get() = 0

    init {
        super.setHorizontalMargin(horizontalMargin)
    }

    fun bindView(item: UnknownBaseCourseContent) {
        itemView.visibility = View.GONE
    }

}