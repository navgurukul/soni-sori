package org.navgurukul.learn.ui.learn.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.navgurukul.learn.courses.db.models.BaseCourseContent
import org.navgurukul.learn.databinding.ItemBaseCourseContentBinding

open class BaseCourseViewHolder(val itemBinding: ItemBaseCourseContentBinding) :
    RecyclerView.ViewHolder(itemBinding.root) {

    fun bind(item: BaseCourseContent) {
        itemBinding.decorContent.visibility = View.GONE

        item.decoration?.let {
            itemBinding.decorContent.visibility = View.VISIBLE
            itemBinding.decorContent.setDecor(it)
        }

    }

    fun addPlaceholder(viewId: Int) {
        itemBinding.placeholderContent.setContentId(viewId)
    }

    fun addView(view: View?) {
        view?.let{
            itemBinding.baseLayoutContent.addView(it)
        }
    }
}
