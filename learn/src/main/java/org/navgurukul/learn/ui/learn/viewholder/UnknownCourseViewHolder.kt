package org.navgurukul.learn.ui.learn.viewholder

import android.view.View
import org.navgurukul.learn.courses.db.models.UnknownBaseCourseContent

class UnknownCourseViewHolder(itemView: View) :
    BaseCourseViewHolder(itemView) {
    fun bindView(item: UnknownBaseCourseContent) {
        itemView.visibility = View.GONE
    }

}