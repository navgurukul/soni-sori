package org.navgurukul.learn.expandablerecyclerviewlist.model

import org.navgurukul.learn.courses.network.model.Course
import org.navgurukul.learn.courses.network.model.Module


data class ParentWrapper(
    var isExpanded:Boolean=false,
    var parentListItem: Module,
    var isInitiallyExpanded: Boolean = false,
    var childListItem: List<Course> = parentListItem.courses
)