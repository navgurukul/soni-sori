package org.navgurukul.learn.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_category.view.tv_category
import org.navgurukul.learn.expandablerecyclerviewlist.adapter.ExpandableRecyclerAdapter
import org.navgurukul.learn.expandablerecyclerviewlist.model.ParentListItem
import org.navgurukul.learn.viewholder.CategoryListViewHolder
import org.navgurukul.learn.viewholder.CategoryViewHolder
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.models.Course
import org.navgurukul.learn.courses.network.model.Module
import org.navgurukul.learn.courses.network.model.PathwayC4CA
import org.navgurukul.learn.ui.learn.adapter.CourseContainer
import org.navgurukul.learn.ui.learn.model.Category
import org.navgurukul.learn.ui.learn.model.CategoryList


class CategoryAdapter :
    ExpandableRecyclerAdapter<CategoryViewHolder, CategoryListViewHolder>() {

    var colors = arrayOf("#29458C", "#FFAD33", "#F091B2")

    override fun onCreateParentViewHolder(
        parentViewGroup: ViewGroup
    ): CategoryViewHolder {
        val view = LayoutInflater.from(parentViewGroup.context)
            .inflate(R.layout.item_category, parentViewGroup, false)
        return CategoryViewHolder(view)
    }

    override fun onCreateChildViewHolder(parentViewGroup: ViewGroup): CategoryListViewHolder {
        val view = LayoutInflater.from(parentViewGroup.context)
            .inflate(R.layout.item_category_list, parentViewGroup, false)
        return CategoryListViewHolder(view)
    }

    override fun onBindParentViewHolder(
        parentViewHolder: CategoryViewHolder,
        position: Int,
        parentListItem: ParentListItem
    ) {
        val data = parentListItem as Category
        parentViewHolder.bind(data)
        //parentListItem.name = data.name
        parentViewHolder.itemView.tv_category.text = data.name
        //parentListItem.name = data.name

//        if(position == 0){
//            parentViewHolder.itemView.setBackgroundColor(Color.parseColor(colors[0]))
//        }else if(position == 1){
//            parentViewHolder.itemView.setBackgroundColor(Color.parseColor(colors[1]))
//        }else if(position == 2){
//            parentViewHolder.itemView.setBackgroundColor(Color.parseColor(colors[2]))
//        }
    }

    override fun onBindChildViewHolder(
        childViewHolder: CategoryListViewHolder,
        position: Int,
        childListItem: Any
    ) {
        val data = childListItem as CategoryList
        childViewHolder.bind(data)
    }

    //    fun submitList(data: List<Module>) {
//        submitList(data)
//    }
//    fun submitList(list: List<PathwayC4CA>) {
//        submitList(list.map { CourseContainer(it) })
//    }
}

data class C4CACourseContainer(val c4ca_course: PathwayC4CA)