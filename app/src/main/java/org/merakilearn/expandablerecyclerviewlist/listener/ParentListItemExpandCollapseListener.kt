package org.merakilearn.expandablerecyclerviewlist.listener

interface ParentListItemExpandCollapseListener{
    fun onParentListItemExpanded(position:Int)
    fun onParentListItemCollapsed(position:Int)

}