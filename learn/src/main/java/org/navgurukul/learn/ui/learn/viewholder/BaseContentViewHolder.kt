package org.navgurukul.learn.ui.learn.viewholder

import android.view.View
import android.view.ViewStub
import androidx.recyclerview.widget.RecyclerView
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.BaseCourseContent
import org.navgurukul.learn.ui.common.DecorationView

open class BaseCourseViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {

    private val decorContent: DecorationView = itemView.findViewById(R.id.decorContent)
    val viewStub: ViewStub = itemView.findViewById(R.id.view_stub)

    fun bind(item: BaseCourseContent) {
        decorContent.visibility = View.GONE

        item.decoration?.let {
            decorContent.visibility = View.VISIBLE
            decorContent.setDecor(it)
        }

    }

    inline fun <reified T : View> populateStub(layoutId: Int): T {
        viewStub.layoutResource = layoutId
        return viewStub.inflate() as T
    }
}
