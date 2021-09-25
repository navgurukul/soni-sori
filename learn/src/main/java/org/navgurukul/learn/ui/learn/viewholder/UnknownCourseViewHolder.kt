package org.navgurukul.learn.ui.learn.viewholder

import android.view.View
import org.navgurukul.learn.courses.db.models.UnknownBaseCourseContent
import org.navgurukul.learn.databinding.ItemBaseCourseContentBinding
import org.navgurukul.learn.ui.learn.adapter.ExerciseContentAdapter

class UnknownCourseViewHolder(itemBinding: ItemBaseCourseContentBinding) :
    BaseCourseViewHolder(itemBinding) {
    fun bindView(item: UnknownBaseCourseContent) {
        itemBinding.baseLayoutContent.visibility = View.GONE
    }

}