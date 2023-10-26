package org.navgurukul.learn.expandablerecyclerviewlist.listener

interface ExpandCollapseListener{
    fun onListItemExpanded(position:Int)
    fun onListItemCollapsed(position:Int)
}