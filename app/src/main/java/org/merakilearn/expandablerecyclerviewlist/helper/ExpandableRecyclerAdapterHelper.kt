package org.merakilearn.expandablerecyclerviewlist.helper

import org.merakilearn.expandablerecyclerviewlist.model.ParentListItem
import org.merakilearn.expandablerecyclerviewlist.model.ParentWrapper

// this helper provide expandable child item list
object ExpandableRecyclerAdapterHelper {
    fun generateParentChildItemList(parentItemList: List<ParentListItem>): List<Any>{
        val parentWrapperList = mutableListOf<Any>()
        parentItemList.forEach {parentListItem->
            val parentWrapper = ParentWrapper(parentListItem = parentListItem)
            parentWrapperList.add(parentWrapper)
            if (parentWrapper.isInitiallyExpanded) {
                parentWrapper.isExpanded = true
                parentWrapper.childListItem.forEach {it?.apply {
                    parentWrapperList.add(it)
                }}
            }
        }
        return parentWrapperList
    }
}