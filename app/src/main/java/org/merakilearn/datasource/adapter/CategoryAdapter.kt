package org.merakilearn.datasource.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import org.merakilearn.R
import org.merakilearn.expandablerecyclerviewlist.adapter.ExpandableRecyclerAdapter
import org.merakilearn.expandablerecyclerviewlist.model.ParentListItem
import org.merakilearn.ui.model.Category
import org.merakilearn.ui.model.CategoryList
import org.merakilearn.ui.viewholder.CategoryListViewHolder
import org.merakilearn.ui.viewholder.CategoryViewHolder


class CategoryAdapter : ExpandableRecyclerAdapter<CategoryViewHolder, CategoryListViewHolder>(){

    override fun onCreateParentViewHolder(parentViewGroup: ViewGroup
    ): CategoryViewHolder {
        val view = LayoutInflater.from(parentViewGroup.context).inflate(R.layout.item_category, parentViewGroup, false)
        return CategoryViewHolder(view)
    }

    override fun onCreateChildViewHolder(parentViewGroup: ViewGroup): CategoryListViewHolder {
        val view = LayoutInflater.from(parentViewGroup.context).inflate(R.layout.item_category_list, parentViewGroup, false)
        return CategoryListViewHolder(view)
    }

    override fun onBindParentViewHolder(parentViewHolder: CategoryViewHolder, position: Int, parentListItem: ParentListItem) {
        val data = parentListItem as Category
        parentViewHolder.bind(data)
    }

    override fun onBindChildViewHolder(childViewHolder: CategoryListViewHolder, position: Int, childListItem: Any) {
        val data = childListItem as CategoryList
        childViewHolder.bind(data)
    }
}