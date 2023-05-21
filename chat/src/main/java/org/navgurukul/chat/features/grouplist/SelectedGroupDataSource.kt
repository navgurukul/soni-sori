package org.navgurukul.chat.features.grouplist

import arrow.core.Option
import org.matrix.android.sdk.api.session.group.model.GroupSummary
import org.navgurukul.chat.core.utils.BehaviorDataSource

class SelectedGroupDataSource : BehaviorDataSource<Option<GroupSummary>>(Option.empty())
