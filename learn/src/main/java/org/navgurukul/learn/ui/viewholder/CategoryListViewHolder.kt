package org.navgurukul.learn.ui.viewholder

import android.view.View
import android.widget.TextView
import org.navgurukul.learn.expandablerecyclerviewlist.viewholder.ChildViewHolder
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.network.model.PathwayC4CA

class CategoryListViewHolder(view:View) : ChildViewHolder(view){
    fun bind(categoryList : PathwayC4CA){
        itemView.findViewById<TextView>(R.id.tvName).text = categoryList.name
    }
}