package org.navgurukul.learn.expandablerecyclerviewlist.model

interface ParentListItem {
    fun getChildItemList():List<*>
    fun isInitiallyExpanded():Boolean
}