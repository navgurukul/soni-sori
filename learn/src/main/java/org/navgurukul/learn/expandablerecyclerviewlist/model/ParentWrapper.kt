package org.navgurukul.learn.expandablerecyclerviewlist.model


data class ParentWrapper(var isExpanded:Boolean=false,
                         var parentListItem: ParentListItem,
                         var isInitiallyExpanded: Boolean = parentListItem.isInitiallyExpanded(),
                         var childListItem: List<*> = parentListItem.getChildItemList())