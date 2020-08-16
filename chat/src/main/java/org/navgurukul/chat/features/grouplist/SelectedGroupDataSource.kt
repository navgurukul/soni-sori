package org.navgurukul.chat.features.grouplist

import arrow.core.Option
import im.vector.matrix.android.api.session.group.model.GroupSummary
import org.navgurukul.chat.core.utils.BehaviorDataSource

class SelectedGroupDataSource : BehaviorDataSource<Option<GroupSummary>>(Option.empty())
