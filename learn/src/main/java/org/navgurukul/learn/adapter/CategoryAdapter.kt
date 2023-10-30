package org.navgurukul.learn.adapter

import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_category_list.view.*
import kotlinx.android.synthetic.main.item_module.view.*
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.network.model.Course
import org.navgurukul.learn.courses.network.model.Module
import org.navgurukul.learn.expandablerecyclerviewlist.adapter.ExpandableRecyclerAdapter
import org.navgurukul.learn.expandablerecyclerviewlist.viewholder.ChildViewHolder
import org.navgurukul.learn.expandablerecyclerviewlist.viewholder.ParentViewHolder


class ModuleViewHolder(itemView: View) : ParentViewHolder(itemView) {
    // Implement the ModuleViewHolder as needed
    fun bindModuleData (module: Module){
        itemView.tv_module.text = module.name
        itemView.ll_module.setBackgroundColor(module.color?.let {
            Color.parseColor(it) } ?: Color.parseColor("#ffff00"))
    }
}

class CourseViewHolder(itemView: View) : ChildViewHolder(itemView) {
    // Implement the CourseViewHolder as needed
    fun bindCourseData (course : Course){
        itemView.nameTv.text = course.name
        ///itemView.progressBar.progress = course.completed_portion

        if(course.completed_portion != null && course.completed_portion > 0){

            //itemView.progressBar.progress = course.completed_portion

            val thumbnail = Glide.with(itemView)
                .load(R.drawable.ic_lock)
            Glide.with(itemView.imageView)
                .load(course.logo)
                .thumbnail(thumbnail)
                .circleCrop()
                .into(itemView.imageView)

        } else {
            val thumbnail = Glide.with(itemView)
                .load(R.drawable.ic_lock)
            Glide.with(itemView.imageView)
                .load(course.logo)
                .thumbnail(thumbnail)
                .circleCrop()
                .into(itemView.imageView)

            //Grey scale
            val colorMatrix =  ColorMatrix()
            colorMatrix.setSaturation(0.0f)
            val filter =  ColorMatrixColorFilter(colorMatrix)
            itemView.imageView.colorFilter = filter
        }
    }
}

class CategoryAdapter :
    ExpandableRecyclerAdapter<ModuleViewHolder, CourseViewHolder>() {

    var colors = arrayOf("#29458C", "#FFAD33", "#F091B2")
    override fun onCreateParentViewHolder(parentViewGroup: ViewGroup): ModuleViewHolder {
        val view = LayoutInflater.from(parentViewGroup.context)
            .inflate(R.layout.item_module, parentViewGroup, false)
        return ModuleViewHolder(view)
    }

    override fun onCreateChildViewHolder(parentViewGroup: ViewGroup): CourseViewHolder {
        val view = LayoutInflater.from(parentViewGroup.context)
            .inflate(R.layout.item_category_list, parentViewGroup, false)
        return CourseViewHolder(view)
    }

    override fun onBindParentViewHolder(
        parentViewHolder: ModuleViewHolder,
        position: Int,
        parentListItem: Module
    ) {
        val module = parentListItem as Module
        parentViewHolder.bindModuleData(module)
    }

    override fun onBindChildViewHolder(
        childViewHolder: CourseViewHolder,
        position: Int,
        childListItem: Any
    ) {
        val course = childListItem as Course
        childViewHolder.bindCourseData(course)
    }

//    override fun onBindParentViewHolder(
//        parentViewHolder: ModuleViewHolder,
//        position: Int,
//        parentListItem: ParentListItem
//    ) {
//        TODO("Not yet implemented")
//    }

}

