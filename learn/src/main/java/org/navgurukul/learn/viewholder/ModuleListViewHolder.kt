package org.navgurukul.learn.viewholder

import android.view.View
import android.widget.TextView
import org.navgurukul.learn.expandablerecyclerviewlist.viewholder.ChildViewHolder
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.network.model.PathwayC4CA

class ModuleListViewHolder(view:View) : ChildViewHolder(view){
//    fun bind(categoryList : CategoryList){
//        itemView.findViewById<TextView>(R.id.nameTv).text = categoryList.name
//    }
    fun bind(pathwayC4CA : PathwayC4CA){
        itemView.findViewById<TextView>(R.id.nameTv).text = pathwayC4CA.name
    }
}