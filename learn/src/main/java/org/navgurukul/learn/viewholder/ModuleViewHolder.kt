//package org.navgurukul.learn.viewholder
//
//import android.view.View
//import android.view.animation.RotateAnimation
//import android.widget.ImageView
//import android.widget.TextView
//import org.navgurukul.learn.expandablerecyclerviewlist.viewholder.ParentViewHolder
//import org.navgurukul.learn.R
//import org.navgurukul.learn.ui.learn.model.Category
//
//class ModuleViewHolder(itemView:View) : ParentViewHolder(itemView) {
//    private lateinit var animation: RotateAnimation
//
////    fun bind(category: Category){
////        itemView.findViewById<TextView>(R.id.tv_category).text = category.name
////    }
//    fun bind(category: Category){
//        itemView.findViewById<TextView>(R.id.tv_category).text = category.name
//    }
//
//    override fun onExpansionToggled(expanded: Boolean) {
//        super.onExpansionToggled(expanded)
//        animation = if (expanded)
//            RotateAnimation(180f,0f, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f)
//        else
//            RotateAnimation(-1 * 180f,0f, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f)
//
//        animation.duration = 200
//        animation.fillAfter = true
//        itemView.findViewById<ImageView>(R.id.iv_arrow_expand).startAnimation(animation)
//
//    }
//
//    override fun setExpanded(expanded: Boolean) {
//        super.setExpanded(expanded)
//        if (expanded)itemView.findViewById<ImageView>(R.id.iv_arrow_expand).rotation = 180f
//        else itemView.findViewById<ImageView>(R.id.iv_arrow_expand).rotation = 0f
//    }
//}