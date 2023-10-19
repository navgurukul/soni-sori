package org.merakilearn.expandablerecyclerviewlist.model

interface ParentListItem {
    fun getChildItemList():List<*>
    fun isInitiallyExpanded():Boolean
}