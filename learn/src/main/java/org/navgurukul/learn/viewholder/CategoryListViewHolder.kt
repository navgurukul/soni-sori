package org.navgurukul.learn.viewholder

import android.view.View
import android.widget.TextView
import org.navgurukul.learn.expandablerecyclerviewlist.viewholder.ChildViewHolder
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.network.model.Module
import org.navgurukul.learn.courses.network.model.PathwayC4CA
import org.navgurukul.learn.ui.learn.model.CategoryList

class CategoryListViewHolder(view:View) : ChildViewHolder(view){
    fun bind(categoryList : CategoryList){
        itemView.findViewById<TextView>(R.id.nameTv).text = categoryList.name
    }
}