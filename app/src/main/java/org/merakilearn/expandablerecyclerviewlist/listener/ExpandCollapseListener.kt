package org.merakilearn.expandablerecyclerviewlist.listener

interface ExpandCollapseListener{
    fun onListItemExpanded(position:Int)
    fun onListItemCollapsed(position:Int)
}