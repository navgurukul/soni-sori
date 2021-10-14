package org.navgurukul.learn.ui.learn.viewholder

import android.view.View
import android.view.ViewStub
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginStart
import androidx.recyclerview.widget.RecyclerView
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.BaseCourseContent
import org.navgurukul.learn.ui.common.DecorationView

abstract class BaseCourseViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {

    private val baseLayout: ConstraintLayout = itemView.findViewById(R.id.base_layout_content)
    private val decorContent: DecorationView = itemView.findViewById(R.id.decorContent)
    val viewStub: ViewStub = itemView.findViewById(R.id.view_stub)
    abstract val horizontalMargin:Int

    val layoutPramas = baseLayout.layoutParams as RecyclerView.LayoutParams
    fun setHorizontalMargin(margin: Int){
        layoutPramas.marginStart = margin
        layoutPramas.marginEnd = margin
    }

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
