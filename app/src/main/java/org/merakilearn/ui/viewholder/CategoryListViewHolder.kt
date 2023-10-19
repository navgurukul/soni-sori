package org.merakilearn.ui.viewholder

import android.view.View
import android.widget.TextView
import org.merakilearn.ui.model.CategoryList
import org.merakilearn.R
import org.merakilearn.expandablerecyclerviewlist.viewholder.ChildViewHolder

class CategoryListViewHolder(view:View) : ChildViewHolder(view){
    fun bind(categoryList : CategoryList){
        itemView.findViewById<TextView>(R.id.nameTv).text = categoryList.name
    }
}